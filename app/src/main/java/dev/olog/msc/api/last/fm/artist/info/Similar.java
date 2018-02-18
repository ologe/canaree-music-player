
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Similar {

    @SerializedName("artist")
    @Expose
    public List<Artist> artist = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Similar() {
    }

    /**
     * 
     * @param artist
     */
    public Similar(List<Artist> artist) {
        super();
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Similar{" +
                "artist=" + artist +
                '}';
    }
}
