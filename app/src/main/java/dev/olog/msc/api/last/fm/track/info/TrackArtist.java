package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackArtist {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;

}
