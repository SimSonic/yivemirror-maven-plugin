package ru.simsonic.minecraft.yivemirror.utils;

import lombok.experimental.UtilityClass;
import ru.simsonic.minecraft.yivemirror.api.ServerDescription;

import java.io.File;

@UtilityClass
public class FileUtils {

    private static final File MAVEN_PLUGIN_HOME = new File(FileUtils.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getFile())
            .getParentFile();

    private static final String CACHE_DIR_NAME = "_cache";

    private static final File CACHE_HOME = new File(MAVEN_PLUGIN_HOME.getParentFile(), CACHE_DIR_NAME);

    public static String getExtension(File file) {
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex > 0
                ? fileName.substring(extensionIndex + 1)
                : "";
    }

    public static File getServerFile(ServerDescription serverDescription, String filename) {
        File catalogForServerType = new File(CACHE_HOME, serverDescription.getType().getCatalog());
        catalogForServerType.mkdirs();
        return new File(catalogForServerType, filename);
    }
}
