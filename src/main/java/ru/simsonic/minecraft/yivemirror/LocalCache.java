package ru.simsonic.minecraft.yivemirror;

import java.io.File;

public class LocalCache {

    private static final String CACHE_DIR_NAME = "_cache";

    private static final File MAVEN_PLUGIN_HOME = new File(LocalCache.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();

    private static final File CACHE_HOME = new File(MAVEN_PLUGIN_HOME.getParentFile(), CACHE_DIR_NAME);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public LocalCache() {
        CACHE_HOME.mkdirs();
    }

    public File getServerFile(String filename) {
        return new File(CACHE_HOME, filename);
    }
}
