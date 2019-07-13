
package dev.olog.data.api.lastfm.artist;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistSearch {

    @SerializedName("results")
    @Expose
    @Nullable
    public Results results;

    public static class Results {

        @Expose
        @Nullable
        public Artistmatches artistmatches;

    }


}
