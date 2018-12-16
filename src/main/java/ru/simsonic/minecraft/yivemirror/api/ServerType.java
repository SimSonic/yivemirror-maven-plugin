package ru.simsonic.minecraft.yivemirror.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerType {

    LOCALLY_PROVIDED("custom", "%s"),

    SPIGOT("spigot", "spigot-%s.jar"),

    PAPER_SPIGOT("paperspigot", "PaperSpigot-%s.jar"),

    THERMOS("thermos", "Thermos-%s.zip");

    private final String catalog;

    private final String filenameFormat;
}
