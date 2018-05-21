package ru.simsonic.minecraft.yivemirror;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

@SuppressWarnings("AccessOfSystemProperties")
@Mojo(name = "run")
public class RunMojo extends AbstractMojo {

    public static final String DEFAULT_SERVER_TYPE = "spigot";
    public static final String DEFAULT_SERVER_VERSION = "latest";
    public static final String DEFAULT_SUBDIRECTORY = "server";
    public static final String DEFAULT_RESOURCES = "server-resources";

    @Parameter(defaultValue = "${project.basedir}", readonly = true, required = true)
    public File projectDirectory;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
    public File buildDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", readonly = true, required = true)
    public String finalName;

    /**
     * Spigot, PaperSpigot, Thermos, etc.
     */
    @Parameter(property = "serverType", defaultValue = DEFAULT_SERVER_TYPE)
    public String serverType;

    /**
     * Like 1.12.2-R0.1-SNAPSHOT-b1612
     */
    @Parameter(property = "serverVersion", defaultValue = DEFAULT_SERVER_VERSION)
    public String serverVersion;

    @Parameter(property = "directory", defaultValue = DEFAULT_SUBDIRECTORY)
    public String directory;

    @Parameter(property = "resources", defaultValue = DEFAULT_RESOURCES)
    public String resources;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            String filename = String.format("%s-%s.jar", serverType, serverVersion);
            File cached = searchInCache(filename);
            if (!cached.isFile()) {
                download(filename, cached);
            }

            File serverDir = new File(buildDirectory, directory);
            install(cached, serverDir);

            run(cached, serverDir);
        } catch (Exception ex) {
            throw new MojoExecutionException("Internal plugin error.", ex);
        }
    }

    private File searchInCache(String filename) {
        String mavenPluginFile = RunMojo.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File cacheDirectory = new File(new File(mavenPluginFile).getParentFile(), "cache");
        cacheDirectory.mkdirs();
        File result = new File(cacheDirectory, filename);
        debug("Location of cached version: %s", result);
        return result;
    }

    private void download(String filename, File target) throws IOException {
        String url = String.format("https://yivesmirror.com/files/%s/%s", serverType, filename);
        info("Downloading from %s into %s", url, target);
        target.getParentFile().mkdirs();
        downloadFile(url, target);
    }

    private static void downloadFile(String sourceUrl, File target) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpUriRequest request = new HttpGet(sourceUrl);
            CloseableHttpResponse response = httpClient.execute(request);
            ReadableByteChannel sourceChannel = Channels.newChannel(response.getEntity().getContent());
            FileChannel destinationChannel = new FileOutputStream(target).getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, Integer.MAX_VALUE);
        }
    }

    private void install(File serverJar, File serverDir) throws IOException, MojoExecutionException {
        getLog().info("Installing ...");

        serverDir.mkdirs();

        File resourcesDir = new File(projectDirectory, resources);
        if (resourcesDir.isDirectory()) {
            copyFolder(resourcesDir, serverDir);
        }

        File compiledPlugin = new File(buildDirectory, finalName + ".jar");
        if (!compiledPlugin.isFile()) {
            throw new MojoExecutionException("Compiled plugin .jar is absent.");
        }
        File pluginsDir = new File(serverDir, "plugins");
        pluginsDir.mkdirs();
        File installedPlugin = new File(pluginsDir, finalName + ".jar");
        Files.copy(compiledPlugin.toPath(), installedPlugin.toPath());
    }

    private void copyFolder(File sourceDir, File targetDir) throws IOException {
        try (Stream<Path> stream = Files.walk(sourceDir.toPath())) {
            stream.filter(f -> f.toFile().isFile())
                    .forEach(sourcePath -> copyFile(sourcePath, targetDir.toPath()));
        }
    }

    private void copyFile(Path sourcePath, Path destPath) {
        try {
            info("Copy file: " + destPath);
            Path relative = sourcePath.relativize(destPath);
            Path resolved = destPath.resolve(relative);
            resolved.toFile().getParentFile().mkdirs();
            Files.copy(sourcePath, resolved);
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void run(File serverJar, File serverDir) throws IOException {
        info("Running ...");

        File pluginsDir = new File(serverDir, "plugins");

        System.setProperty("com.mojang.eula.agree", "true");
        System.setProperty("log4j.skipJansi", "true");
        System.setProperty("IReallyKnowWhatIAmDoingISwear", "true");

        Object[] arguments = {new String[]{
                "--config", new File(serverDir, "server.properties").getAbsolutePath(),
                "--bukkit-settings", new File(serverDir, "bukkit.yml").getAbsolutePath(),
                "--spigot-settings", new File(serverDir, "spigot.yml").getAbsolutePath(),
                "--commands-settings", new File(serverDir, "commands.yml").getAbsolutePath(),
                "--plugins", pluginsDir.getAbsolutePath(),
                "--world-dir", serverDir.getAbsolutePath(),
                "--nojline",
        }};

        URL[] urls = Collections.singletonList(serverJar.toURI().toURL()).toArray(new URL[0]);
        try (URLClassLoader childClassLoader = new URLClassLoader(urls)) {

            String mainClassName = getJarMainClass(serverJar);
            Class<?> mainClass = childClassLoader.loadClass(mainClassName);
            Method method = mainClass.getMethod("main", String[].class);

            method.invoke(null, arguments);

            Thread.currentThread().join();
        } catch (Exception ex) {
            error(ex.toString());
        }
    }

    private static String getJarMainClass(File serverJar) throws IOException {
        try (JarFile jarFile = new JarFile(serverJar)) {
            return Optional.ofNullable(jarFile.getManifest())
                    .map(Manifest::getMainAttributes)
                    .map(m -> m.getValue("Main-Class"))
                    .orElseThrow(() -> new IOException("Server core has no set up main class."));
        }
    }

    private void debug(String message) {
        getLog().debug(String.format("%s", message));
    }

    private void info(String message) {
        getLog().info(String.format("%s", message));
    }

    private void warn(String message) {
        getLog().warn(String.format("%s", message));
    }

    private void error(String message) {
        getLog().error(String.format("%s", message));
    }

    private void debug(String format, Object... args) {
        getLog().debug(String.format(format, args));
    }

    private void info(String format, Object... args) {
        getLog().info(String.format(format, args));
    }

    private void warn(String format, Object... args) {
        getLog().warn(String.format(format, args));
    }

    private void error(String format, Object... args) {
        getLog().error(String.format(format, args));
    }
}
