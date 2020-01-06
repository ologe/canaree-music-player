
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artistmatches {

    @SerializedName("artist")
    public List<Artist> artist = new ArrayList<>();

    public static class Artist {

        public String name;

        public String mbid;

        public List<Image> image = new ArrayList<>();

        public static class Image {

            @SerializedName("#text")
            public String text;
            public String size;
        }

    }

}
