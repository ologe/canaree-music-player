
package dev.olog.data.model.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albummatches {

    @SerializedName("album")
    public List<Album> album = new ArrayList<>();

    public static class Album {

        public String name;

        public String artist;

        public String url;

        public List<Image> image = new ArrayList<>();

        public String mbid;

        public static class Image {

            @SerializedName("#text")
            public String text;

        }


    }


}
