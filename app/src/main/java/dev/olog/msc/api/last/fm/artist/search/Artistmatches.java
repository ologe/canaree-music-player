
package dev.olog.msc.api.last.fm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artistmatches {

    @SerializedName("artist")
    @Expose
    public List<Artist> artist = new ArrayList<Artist>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artistmatches() {
    }

    /**
     * 
     * @param artist
     */
    public Artistmatches(List<Artist> artist) {
        super();
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Artistmatches{" +
                "artist=" + artist +
                '}';
    }
}
