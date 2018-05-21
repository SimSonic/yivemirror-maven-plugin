package ru.simsonic.minecraft.yivemirror;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor
@Getter
public class ServerEnvironment {

    private static final String PLUGINS_HOME = "plugins";

    private final File serverDirectory;

    private final File serverJar;

    public File getPluginsDirectory() {
        return new File(serverDirectory, PLUGINS_HOME);
    }
}
