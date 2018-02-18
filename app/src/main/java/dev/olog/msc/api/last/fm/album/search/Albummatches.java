
package dev.olog.msc.api.last.fm.album.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albummatches {

    @SerializedName("album")
    @Expose
    public List<Album> album = new ArrayList<Album>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Albummatches() {
    }

    /**
     * 
     * @param album
     */
    public Albummatches(List<Album> album) {
        super();
        this.album = album;
    }

    @Override
    public String toString() {
        return "Albummatches{" +
                "album=" + album +
                '}';
    }
}
