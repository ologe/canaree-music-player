
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artistmatches {

    @SerializedName("artist")
    @Expose
    public List<Artist> artist = new ArrayList<>();

    public static class Artist {

        @Expose
        public String name;

        @Expose
        public String mbid;

        @Expose
        public List<Image> image = new ArrayList<>();

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;
            @Expose
            public String size;
        }

    }

}
