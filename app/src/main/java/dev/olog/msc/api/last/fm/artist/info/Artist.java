
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("mbid")
    @Expose
    public String mbid;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("image")
    @Expose
    public List<Image> image = new ArrayList<Image>();
    @SerializedName("streamable")
    @Expose
    public String streamable;
    @SerializedName("ontour")
    @Expose
    public String ontour;
    @SerializedName("stats")
    @Expose
    public Stats stats;
    @SerializedName("similar")
    @Expose
    public Similar similar;
    @SerializedName("tags")
    @Expose
    public Tags tags;
    @SerializedName("bio")
    @Expose
    public Bio bio;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artist() {
    }

    /**
     * 
     * @param tags
     * @param ontour
     * @param mbid
     * @param bio
     * @param stats
     * @param name
     * @param streamable
     * @param image
     * @param url
     * @param similar
     */
    public Artist(String name, String mbid, String url, List<Image> image, String streamable, String ontour, Stats stats, Similar similar, Tags tags, Bio bio) {
        super();
        this.name = name;
        this.mbid = mbid;
        this.url = url;
        this.image = image;
        this.streamable = streamable;
        this.ontour = ontour;
        this.stats = stats;
        this.similar = similar;
        this.tags = tags;
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "name='" + name + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", image=" + image +
                ", streamable='" + streamable + '\'' +
                ", ontour='" + ontour + '\'' +
                ", stats=" + stats +
                ", similar=" + similar +
                ", tags=" + tags +
                ", bio=" + bio +
                '}';
    }
}
