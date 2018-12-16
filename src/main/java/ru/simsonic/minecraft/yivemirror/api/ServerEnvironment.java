package ru.simsonic.minecraft.yivemirror.api;

import lombok.Value;

import java.io.File;

@Value
public class ServerEnvironment {

    private static final String PLUGINS_HOME = "plugins";

    File serverDirectory;

    File serverJar;

    public File getPluginsDirectory() {
        return new File(serverDirectory, PLUGINS_HOME);
    }
}
