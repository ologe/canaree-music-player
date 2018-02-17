package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("streamable")
    @Expose
    public TrackStreamable streamable;
    @SerializedName("listeners")
    @Expose
    public String listeners;
    @SerializedName("playcount")
    @Expose
    public String playcount;
    @SerializedName("artist")
    @Expose
    public TrackArtist artist;
    @SerializedName("album")
    @Expose
    public TrackAlbum album;
    @SerializedName("toptags")
    @Expose
    public TrackTopTags toptags;
    @SerializedName("wiki")
    @Expose
    public TrackWiki wiki;

}