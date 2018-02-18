
package dev.olog.msc.api.last.fm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistSearch {

    @SerializedName("results")
    @Expose
    public Results results;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ArtistSearch() {
    }

    /**
     * 
     * @param results
     */
    public ArtistSearch(Results results) {
        super();
        this.results = results;
    }

    @Override
    public String toString() {
        return "ArtistSearch{" +
                "results=" + results +
                '}';
    }
}
