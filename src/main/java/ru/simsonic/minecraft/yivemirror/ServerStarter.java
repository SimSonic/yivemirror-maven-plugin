package ru.simsonic.minecraft.yivemirror;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ServerStarter {

    private final LogWrapper logger;

    public ServerStarter(LogWrapper logger) {
        this.logger = logger;
    }

    public void run(ServerEnvironment environment) throws IOException {
        // Allow old builds
        System.setProperty("IReallyKnowWhatIAmDoingISwear", "true");

        // Agree with EULA
        System.setProperty("com.mojang.eula.agree", "true");

        // Disable jansi
        System.setProperty("log4j.skipJansi", "true");

        // Disable colored output
        System.setProperty("jline.terminal", "jline.UnsupportedTerminal");

        File serverDirectory = environment.getServerDirectory();
        File serverJar = environment.getServerJar();
        File pluginsDirectory = environment.getPluginsDirectory();

        File serverProperties = new File(serverDirectory, "server.properties");
        File bukkitYaml = new File(serverDirectory, "bukkit.yml");
        File spigotYaml = new File(serverDirectory, "spigot.yml");
        File paperYaml = new File(serverDirectory, "paper.yml");
        File commandsYaml = new File(serverDirectory, "commands.yml");

        List<String> commandLineParameters = new ArrayList<>(32);

        if (serverProperties.isFile()) {
            commandLineParameters.add("--config");
            commandLineParameters.add(serverProperties.getAbsolutePath());
        }
        if (bukkitYaml.isFile()) {
            commandLineParameters.add("--bukkit-settings");
            commandLineParameters.add(bukkitYaml.getAbsolutePath());
        }
        if (spigotYaml.isFile()) {
            commandLineParameters.add("--spigot-settings");
            commandLineParameters.add(spigotYaml.getAbsolutePath());
        }
        if (paperYaml.isFile()) {
            commandLineParameters.add("--paper-settings");
            commandLineParameters.add(paperYaml.getAbsolutePath());
        }
        if (commandsYaml.isFile()) {
            commandLineParameters.add("--commands-settings");
            commandLineParameters.add(commandsYaml.getAbsolutePath());
        }
        commandLineParameters.add("--plugins");
        commandLineParameters.add(pluginsDirectory.getAbsolutePath());
        commandLineParameters.add("--world-dir");
        commandLineParameters.add(serverDirectory.getAbsolutePath());
        commandLineParameters.add("--nojline");

        commandLineParameters.forEach(p -> logger.debug("Command line param: "));
        logger.debug("Server jar file: " + serverJar.getAbsolutePath());

        URL[] urls = Collections.singletonList(serverJar.toURI().toURL()).toArray(new URL[0]);
        try (URLClassLoader childClassLoader = new URLClassLoader(urls)) {

            String mainClassName = getJarMainClass(serverJar);
            Class<?> mainClass = childClassLoader.loadClass(mainClassName);
            Method method = mainClass.getMethod("main", String[].class);

            Object[] arguments = {
                    commandLineParameters.toArray(new String[0])
            };
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
