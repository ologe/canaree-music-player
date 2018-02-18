
package dev.olog.msc.api.last.fm.album.info;

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
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image")
    @Expose
    public List<Image> image = new ArrayList<Image>();
    @SerializedName("listeners")
    @Expose
    public String listeners;
    @SerializedName("playcount")
    @Expose
    public String playcount;
    @SerializedName("tracks")
    @Expose
    public Tracks tracks;
    @SerializedName("tags")
    @Expose
    public Tags tags;
    @SerializedName("wiki")
    @Expose
    public Wiki wiki;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Album() {
    }

    /**
     * 
     * @param tags
     * @param listeners
     * @param mbid
     * @param name
     * @param image
     * @param playcount
     * @param tracks
     * @param artist
     * @param wiki
     * @param url
     */
    public Album(String name, String artist, String mbid, String url, List<Image> image, String listeners, String playcount, Tracks tracks, Tags tags, Wiki wiki) {
        super();
        this.name = name;
        this.artist = artist;
        this.mbid = mbid;
        this.url = url;
        this.image = image;
        this.listeners = listeners;
        this.playcount = playcount;
        this.tracks = tracks;
        this.tags = tags;
        this.wiki = wiki;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", listeners='" + listeners + '\'' +
                ", playcount='" + playcount + '\'' +
                ", tracks=" + tracks +
                ", tags=" + tags +
                ", wiki=" + wiki +
                '}';
    }
}
