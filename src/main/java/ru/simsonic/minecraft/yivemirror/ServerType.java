package ru.simsonic.minecraft.yivemirror;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServerType {

    SPIGOT("spigot", "spigot-%s.jar"),

    PAPER_SPIGOT("paperspigot", "PaperSpigot-%s.jar"),

    THERMOS("thermos", "Thermos-%s.zip"),

    LOCALLY_PROVIDED("custom", "%s.jar");

    private final String catalog;

    private final String filenameFormat;
}
