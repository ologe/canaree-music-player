
package dev.olog.data.model.lastfm;

import com.google.gson.annotations.SerializedName;

public class ArtistSearch {

    @SerializedName("results")
    public Results results;

    public static class Results {

        public Artistmatches artistmatches;

    }


}
