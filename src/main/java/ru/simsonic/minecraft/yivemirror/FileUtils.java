package ru.simsonic.minecraft.yivemirror;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class FileUtils {

    public static String getExtension(File file) {
        String fileName = file.getName();
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex > 0
                ? fileName.substring(extensionIndex + 1)
                : "";
    }
}
