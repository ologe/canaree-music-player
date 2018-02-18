
package dev.olog.msc.api.last.fm.album.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Album {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("artist")
    @Expose
    public String artist;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image")
    @Expose
    public List<Image> image = new ArrayList<Image>();
    @SerializedName("streamable")
    @Expose
    public String streamable;
    @SerializedName("mbid")
    @Expose
    public String mbid;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Album() {
    }

    /**
     * 
     * @param mbid
     * @param name
     * @param streamable
     * @param image
     * @param artist
     * @param url
     */
    public Album(String name, String artist, String url, List<Image> image, String streamable, String mbid) {
        super();
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.image = image;
        this.streamable = streamable;
        this.mbid = mbid;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", streamable='" + streamable + '\'' +
                ", mbid='" + mbid + '\'' +
                '}';
    }
}
