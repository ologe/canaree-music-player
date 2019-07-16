
package dev.olog.data.api.lastfm.album;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albummatches {

    @SerializedName("album")
    @Expose
    public List<Album> album = new ArrayList<>();

    public static class Album {

        @Expose
        public String name;

        @Expose
        public String artist;

        @Expose
        public String url;

        @Expose
        public List<Image> image = new ArrayList<>();

        @Expose
        public String mbid;

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;

        }


    }


}
