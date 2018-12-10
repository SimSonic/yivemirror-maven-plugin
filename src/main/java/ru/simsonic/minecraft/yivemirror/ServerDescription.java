package ru.simsonic.minecraft.yivemirror;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor
@Getter
@ToString
public class ServerDescription {

    private final ServerType type;

    private final String version;

    static ServerDescription forLocallyProvided(String serverJar) {
        File serverJarFile = new File(serverJar);
        if (serverJarFile.isFile()) {
            try {
                byte[] hash = hashFile(serverJarFile);
                String version = binaryToHex(hash);
                return new ServerDescription(ServerType.LOCALLY_PROVIDED, version);
            } catch (IOException | NoSuchAlgorithmException ignored) {
            }
        }
        return null;
    }

    private static byte[] hashFile(File file) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(file.toPath());
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(data);
    }

    @SuppressWarnings("MagicNumber")
    private static String binaryToHex(byte[] hash) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hash) {
            stringBuilder.append(Character.forDigit(b >> 4 & 0x0F, 16));
            stringBuilder.append(Character.forDigit(b & 0x0F, 16));
        }
        return stringBuilder.toString();
    }

    static ServerDescription forRemote(String serverType, String serverVersion) {
        return new ServerDescription(
                ServerType.valueOf(serverType.toUpperCase()),
                serverVersion);
    }
}
