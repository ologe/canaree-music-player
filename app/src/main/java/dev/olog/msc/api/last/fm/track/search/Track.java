
package dev.olog.msc.api.last.fm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Track {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("streamable")
    @Expose
    public String streamable;
    @SerializedName("listeners")
    @Expose
    public String listeners;
    @SerializedName("image")
    @Expose
    public List<Image> image = new ArrayList<Image>();
    @SerializedName("mbid")
    @Expose
    public String mbid;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Track() {
    }

    /**
     * 
     * @param listeners
     * @param mbid
     * @param name
     * @param image
     * @param streamable
     * @param artist
     * @param url
     */
    public Track(String name, String artist, String url, String streamable, String listeners, List<Image> image, String mbid) {
        super();
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.streamable = streamable;
        this.listeners = listeners;
        this.image = image;
        this.mbid = mbid;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", streamable='" + streamable + '\'' +
                ", listeners='" + listeners + '\'' +
                ", image=" + image +
                ", mbid='" + mbid + '\'' +
                '}';
    }
}
