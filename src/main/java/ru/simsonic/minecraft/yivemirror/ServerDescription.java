package ru.simsonic.minecraft.yivemirror;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ServerDescription {

    private final ServerType type;

    private final String version;

    static ServerDescription from(String serverType, String serverVersion) {
        return new ServerDescription(
                ServerType.valueOf(serverType.toUpperCase()),
                serverVersion);
    }
}
