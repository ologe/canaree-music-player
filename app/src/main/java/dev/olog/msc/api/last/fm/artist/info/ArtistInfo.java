
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistInfo {

    @SerializedName("artist")
    @Expose
    public Artist artist;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ArtistInfo() {
    }

    /**
     * 
     * @param artist
     */
    public ArtistInfo(Artist artist) {
        super();
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "ArtistInfo{" +
                "artist=" + artist +
                '}';
    }
}
