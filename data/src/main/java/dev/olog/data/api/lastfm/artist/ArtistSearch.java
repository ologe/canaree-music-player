
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.SerializedName;

public class ArtistSearch {

    @SerializedName("results")
    public Results results;

    public static class Results {

        public Artistmatches artistmatches;

    }


}
