
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("@attr")
    @Expose
    public Attr attr;
    @SerializedName("streamable")
    @Expose
    public Streamable streamable;
    @SerializedName("artist")
    @Expose
    public Artist artist;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Track() {
    }

    /**
     * 
     * @param duration
     * @param name
     * @param streamable
     * @param attr
     * @param artist
     * @param url
     */
    public Track(String name, String url, String duration, Attr attr, Streamable streamable, Artist artist) {
        super();
        this.name = name;
        this.url = url;
        this.duration = duration;
        this.attr = attr;
        this.streamable = streamable;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", duration='" + duration + '\'' +
                ", attr=" + attr +
                ", streamable=" + streamable +
                ", artist=" + artist +
                '}';
    }
}
