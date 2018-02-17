package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackImage {

    @SerializedName("#text")
    @Expose
    public String text;
    @SerializedName("size")
    @Expose
    public String size;

}
