
package dev.olog.data.api.lastfm.artist;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artistmatches {

    @SerializedName("artist")
    @Expose
    @Nullable
    public List<Artist> artist = new ArrayList<>();

    public static class Artist {

        @Expose
        @Nullable
        public String name;

        @Expose
        @Nullable
        public String mbid;

        @Expose
        @Nullable
        public List<Image> image = new ArrayList<>();

        public static class Image {

            @SerializedName("#text")
            @Expose
            @Nullable
            public String text;
            @Expose
            @Nullable
            public String size;
        }

    }

}
