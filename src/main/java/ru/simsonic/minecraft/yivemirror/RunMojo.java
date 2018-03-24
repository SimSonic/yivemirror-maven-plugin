package ru.simsonic.minecraft.yivemirror;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

@Mojo(name = "run")
public class RunMojo extends AbstractMojo {

    public static final String DEFAULT_SERVER_TYPE = "spigot";
    public static final String DEFAULT_SERVER_VERSION = "latest";

    /**
     * Spigot, PaperSpigot, Thermos, etc.
     */
    @Parameter(property = "serverType", defaultValue = DEFAULT_SERVER_TYPE)
    public String serverType;

    /**
     * Like 1.12.2-R0.1-SNAPSHOT-b1612
     */
    @Parameter(property = "serverVersion", defaultValue = DEFAULT_SERVER_VERSION)
    public String serverVersion;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            download();
            install();
            run();
        } catch (Exception ex) {
            throw new MojoExecutionException("Internal plugin error.", ex);
        }
    }

    private void download() throws IOException {
        info("Downloading '%s' version '%s' ...", serverType, serverVersion);
        String filename = String.format("%s-%s.jar", serverType, serverVersion);
        String url = String.format("https://yivesmirror.com/files/%s/%s", serverType, filename);
        info("Downloading url = %s", url);
        File target = new File("target", filename);
        info("Target file is %s", target.getAbsolutePath());
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpUriRequest request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);
            ReadableByteChannel sourceChannel = Channels.newChannel(response.getEntity().getContent());
            FileChannel destinationChannel = new FileOutputStream(target).getChannel();
            destinationChannel.transferFrom(sourceChannel, 0, Integer.MAX_VALUE);
        }
    }

    private void install() {
        getLog().info("Installing ...");
    }

    private void run() {
        getLog().info("Running ...");
    }

    private void debug(String format, Object... args) {
        getLog().debug(String.format(format, args));
    }

    private void info(String format, Object... args) {
        getLog().info(String.format(format, args));
    }

    private void warn(String format, Object... args) {
        getLog().warn(String.format(format, args));
    }

    private void error(String format, Object... args) {
        getLog().error(String.format(format, args));
    }
}
