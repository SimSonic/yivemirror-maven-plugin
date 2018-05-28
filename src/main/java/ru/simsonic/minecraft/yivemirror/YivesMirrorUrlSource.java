package ru.simsonic.minecraft.yivemirror;

public class YivesMirrorUrlSource {

    public String getFilenameForServer(ServerDescription serverDescription) {
        switch (serverDescription.getType()) {
            case SPIGOT:
                return String.format("spigot-%s.jar", serverDescription.getVersion());
            case PAPER_SPIGOT:
                return String.format("PaperSpigot-%s.jar", serverDescription.getVersion());
            default:
                throw new IllegalArgumentException("Not supported yet.");
        }
    }

    public String getUrlForServer(ServerDescription serverDescription) {
        String filename = getFilenameForServer(serverDescription);

        switch (serverDescription.getType()) {
            case SPIGOT:
                return String.format("https://yivesmirror.com/files/spigot/%s", filename);
            case PAPER_SPIGOT:
                return String.format("https://yivesmirror.com/files/paperspigot/%s", filename);
            default:
                throw new IllegalArgumentException("Not supported yet.");
        }
    }
}
