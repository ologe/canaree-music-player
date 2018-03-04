package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Album {

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
    public List<Image> image = new ArrayList<>();
    @SerializedName("@attr")
    @Expose
    public Attr attr;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Album() {
    }

    /**
     * 
     * @param title
     * @param mbid
     * @param image
     * @param attr
     * @param artist
     * @param url
     */
    public Album(String artist, String title, String mbid, String url, List<Image> image, Attr attr) {
        super();
        this.artist = artist;
        this.title = title;
        this.mbid = mbid;
        this.url = url;
        this.image = image;
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Album{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", attr=" + attr +
                '}';
    }
}
