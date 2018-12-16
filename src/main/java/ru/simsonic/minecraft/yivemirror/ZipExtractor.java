package ru.simsonic.minecraft.yivemirror;

import lombok.RequiredArgsConstructor;
import ru.simsonic.minecraft.yivemirror.utils.FileUtils;
import ru.simsonic.minecraft.yivemirror.utils.LogWrapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequiredArgsConstructor
public class ZipExtractor {

    private final LogWrapper logger;

    public File unzipIfNecessary(File zipOrNotZipFile) throws IOException {
        if (!zipOrNotZipFile.isFile()) {
            logger.warn("Is not a file!: " + zipOrNotZipFile);
            return zipOrNotZipFile;
        }

        String fileName = zipOrNotZipFile.getName();
        String extension = FileUtils.getExtension(zipOrNotZipFile).toLowerCase();
        String nameOnly = fileName.substring(0, fileName.lastIndexOf('.'));
        if (!"zip".equals(extension)) {
            return zipOrNotZipFile;
        }

        return extractFromZipAndGetSingleJar(zipOrNotZipFile, nameOnly + "_extracted");
    }

    private File extractFromZipAndGetSingleJar(File zip, String destination) throws IOException {
        logger.debug(String.format("Extracting %s contents into %s", zip, destination));
        File directory = new File(zip.getParent(), destination);
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();
        unzipAll(zip, directory);

        try (Stream<Path> stream = Files.list(directory.toPath())) {
            return stream
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().toLowerCase().endsWith(".jar"))
                    .findAny()
                    .orElse(zip);
        }
    }

    public void unzipAll(File zipFilePath, File unzipLocation) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                File file = new File(unzipLocation, entry.getName());
                if (!entry.isDirectory()) {
                    file.getParentFile().mkdirs();
                    unzipEntry(zipInputStream, file, entry);
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }

    public void unzipEntry(ZipInputStream zis, File target, ZipEntry entry) throws IOException {
        if (target.isFile() && target.length() == entry.getSize()) {
            logger.debug("File already exists, skipping: " + target.getName());
            return;
        }
        logger.debug("Extracting file from archive: " + target.getName());
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target))) {
            //noinspection MagicNumber
            byte[] bytesIn = new byte[1024];
            int read;
            while ((read = zis.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
