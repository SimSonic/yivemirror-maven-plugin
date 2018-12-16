package ru.simsonic.minecraft.yivemirror.remotes;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.MojoExecutionException;
import ru.simsonic.minecraft.yivemirror.api.RemoteDescription;
import ru.simsonic.minecraft.yivemirror.api.ServerDescription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class YivesMirrorUrlSource {

    private static final String DIRECT_DOWNLOAD_URL = "https://yivesmirror.com/files/";

    private static final String API_GET_INFO_URL = "https://yivesmirror.com/api/file/";

    public String getFilenameForServer(ServerDescription serverDescription) {
        return String.format(serverDescription.getType().getFilenameFormat(), serverDescription.getVersion());
    }

    public String getDownloadUrlForServer(ServerDescription serverDescription) {
        return DIRECT_DOWNLOAD_URL + getUrlPartForServer(serverDescription);
    }

    public RemoteDescription fetchInfoAboutServer(ServerDescription serverDescription) throws MojoExecutionException {
        String infoRequestUrl = API_GET_INFO_URL + getUrlPartForServer(serverDescription);

        //noinspection resource
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(infoRequestUrl))) {
            String content = readContentAsString(response.getEntity());
            return new Gson().fromJson(content, RemoteDescription.class);
        } catch (Exception ignored) {
        }

        throw new MojoExecutionException("Yive's Mirror doesn't know about such server: " + serverDescription);
    }

    private String getUrlPartForServer(ServerDescription serverDescription) {
        String filename = getFilenameForServer(serverDescription);
        return String.format("%s/%s", serverDescription.getType().getCatalog(), filename);
    }

    private static String readContentAsString(HttpEntity entity) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }
}
