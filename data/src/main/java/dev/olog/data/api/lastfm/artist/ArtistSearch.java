
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistSearch {

    @SerializedName("results")
    @Expose
    public Results results;

    public static class Results {

        @Expose
        public Artistmatches artistmatches;

    }


}
