package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackStreamable {

    @SerializedName("#text")
    @Expose
    public String text;
    @SerializedName("fulltrack")
    @Expose
    public String fulltrack;

}
