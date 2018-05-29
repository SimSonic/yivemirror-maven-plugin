package ru.simsonic.minecraft.yivemirror;

import lombok.Getter;

@Getter
public enum ServerType {

    SPIGOT("spigot", "spigot-%s.jar"),

    PAPER_SPIGOT("paperspigot", "PaperSpigot-%s.jar"),

    THERMOS("thermos", "Thermos-%s.zip");

    private final String catalog;

    private final String filenameFormat;

    ServerType(String catalog, String filenameFormat) {
        this.catalog = catalog;
        this.filenameFormat = filenameFormat;
    }
}
