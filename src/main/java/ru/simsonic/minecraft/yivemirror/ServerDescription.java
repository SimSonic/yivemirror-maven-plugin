package ru.simsonic.minecraft.yivemirror;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerDescription {

    private final ServerType type;

    private final String version;
}
