
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
    
    public AlbumSearch(Results results) {
        super();
        this.results = results;
    }

    public static class Results {

        @SerializedName("albummatches")
        @Expose
        public Albummatches albummatches;

        /**
         * No args constructor for use in serialization
         *
         */
        public Results() {
        }

        public Results(Albummatches albummatches) {
            super();
            this.albummatches = albummatches;
        }
    }


}
