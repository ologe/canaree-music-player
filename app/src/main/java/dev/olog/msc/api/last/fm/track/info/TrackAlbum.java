package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.w3c.dom.Attr;

import java.util.List;

public class TrackAlbum {

    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image")
    @Expose
    public List<TrackImage> image = null;
    @SerializedName("@attr")
    @Expose
    public Attr attr;

}
