package ru.simsonic.minecraft.yivemirror;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ServerStarter {

    private final LogWrapper logger;

    public ServerStarter(LogWrapper logger) {
        this.logger = logger;
    }

    public void run(ServerEnvironment environment) throws IOException {
        System.setProperty("com.mojang.eula.agree", "true");
        System.setProperty("log4j.skipJansi", "true");
        System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
        System.setProperty("IReallyKnowWhatIAmDoingISwear", "true");

        File serverDirectory = environment.getServerDirectory();
        File serverJar = environment.getServerJar();
        File pluginsDirectory = environment.getPluginsDirectory();

        Object[] arguments = {new String[]{
                "--config", new File(serverDirectory, "server.properties").getAbsolutePath(),
                "--bukkit-settings", new File(serverDirectory, "bukkit.yml").getAbsolutePath(),
                "--spigot-settings", new File(serverDirectory, "spigot.yml").getAbsolutePath(),
                "--paper-settings", new File(serverDirectory, "paper.yml").getAbsolutePath(),
                "--commands-settings", new File(serverDirectory, "commands.yml").getAbsolutePath(),
                "--plugins", pluginsDirectory.getAbsolutePath(),
                "--world-dir", serverDirectory.getAbsolutePath(),
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
            logger.error(ex.toString());
        }
    }

    private static String getJarMainClass(File jar) throws IOException {
        try (JarFile jarFile = new JarFile(jar)) {
            return Optional.ofNullable(jarFile.getManifest())
                    .map(Manifest::getMainAttributes)
                    .map(m -> m.getValue("Main-Class"))
                    .orElseThrow(() -> new IOException("Server core has no set up main class."));
        }
    }
}
