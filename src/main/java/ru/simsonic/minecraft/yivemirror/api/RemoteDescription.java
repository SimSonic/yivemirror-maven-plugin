package ru.simsonic.minecraft.yivemirror.api;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RemoteDescription {

    @SerializedName("size_human")
    private String sizeReadable;

    @SerializedName("size_bytes")
    private Long sizeInBytes;

    @SerializedName("file_name")
    private String filename;

    @SerializedName("date_human")
    private String dateReadable;

    @SerializedName("date_epoch")
    private Long timestamp;

    @SerializedName("mc_version")
    private String version;
}
