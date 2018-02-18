
package dev.olog.msc.api.last.fm.album.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AlbumSearch {

    @SerializedName("results")
    @Expose
    public Results results;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AlbumSearch() {
    }

    /**
     * 
     * @param results
     */
    public AlbumSearch(Results results) {
        super();
        this.results = results;
    }

    @Override
    public String toString() {
        return "AlbumSearch{" +
                "results=" + results +
                '}';
    }
}
