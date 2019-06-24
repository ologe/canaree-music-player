
package dev.olog.data.api.lastfm.artist.search;

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

    public ArtistSearch(Results results) {
        super();
        this.results = results;
    }

    public static class Results {

        @SerializedName("artistmatches")
        @Expose
        public Artistmatches artistmatches;


        /**
         * No args constructor for use in serialization
         *
         */
        public Results() {
        }

        public Results(Artistmatches artistmatches) {
            super();
            this.artistmatches = artistmatches;
        }

    }


}
