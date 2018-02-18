
package dev.olog.msc.api.last.fm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackSearch {

    @SerializedName("results")
    @Expose
    public Results results;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackSearch() {
    }

    /**
     * 
     * @param results
     */
    public TrackSearch(Results results) {
        super();
        this.results = results;
    }

    @Override
    public String toString() {
        return "TrackSearch{" +
                "results=" + results +
                '}';
    }
}
