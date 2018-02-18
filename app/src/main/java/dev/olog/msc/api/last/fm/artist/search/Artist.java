
package dev.olog.msc.api.last.fm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("listeners")
    @Expose
    public String listeners;
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("streamable")
    @Expose
    public String streamable;
    @SerializedName("image")
    @Expose
    public List<Image> image = new ArrayList<Image>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artist() {
    }

    /**
     * 
     * @param listeners
     * @param mbid
     * @param name
     * @param image
     * @param streamable
     * @param url
     */
    public Artist(String name, String listeners, String mbid, String url, String streamable, List<Image> image) {
        super();
        this.name = name;
        this.listeners = listeners;
        this.mbid = mbid;
        this.url = url;
        this.streamable = streamable;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", listeners='" + listeners + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", streamable='" + streamable + '\'' +
                ", image=" + image +
                '}';
    }
}
