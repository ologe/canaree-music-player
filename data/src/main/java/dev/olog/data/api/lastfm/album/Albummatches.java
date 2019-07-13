
package dev.olog.data.api.lastfm.album;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albummatches {

    @SerializedName("album")
    @Expose
    @Nullable
    public List<Album> album = new ArrayList<>();

    public static class Album {

        @Expose
        @Nullable
        public String name;

        @Expose
        @Nullable
        public String artist;

        @Expose
        @Nullable
        public String url;

        @Expose
        @Nullable
        public List<Image> image = new ArrayList<>();

        @Expose
        @Nullable
        public String mbid;

        public static class Image {

            @SerializedName("#text")
            @Expose
            @Nullable
            public String text;

        }


    }


}
