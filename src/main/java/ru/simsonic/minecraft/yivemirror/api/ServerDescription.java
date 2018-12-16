package ru.simsonic.minecraft.yivemirror.api;

import lombok.SneakyThrows;
import lombok.Value;
import ru.simsonic.minecraft.yivemirror.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Value
public class ServerDescription {

    ServerType type;

    String version;

    public static ServerDescription forRemote(String serverType, String serverVersion) {
        return new ServerDescription(
                ServerType.valueOf(serverType.toUpperCase()),
                serverVersion);
    }

    @SneakyThrows
    public static ServerDescription forLocallyProvided(String serverJar) {
        File serverJarFile = new File(serverJar);
        if (serverJarFile.isFile()) {
            byte[] hash = hashFile(serverJarFile);
            String version = String.format("%s.%s", binaryToHex(hash), FileUtils.getExtension(serverJarFile));
            return new ServerDescription(ServerType.LOCALLY_PROVIDED, version);
        }

        throw new IllegalAccessException(String.format("File %s is not found.", serverJar));
    }

    private static byte[] hashFile(File file) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(file.toPath());
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(data);
    }

    private static String binaryToHex(byte[] hash) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte octet : hash) {
            stringBuilder.append(String.format("%02x", octet));
        }
        return stringBuilder.toString();
    }
}
