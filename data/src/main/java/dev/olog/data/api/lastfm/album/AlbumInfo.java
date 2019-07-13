
package dev.olog.data.api.lastfm.album;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo {

    @Expose
    @Nullable
    public Album album;

    public static class Album {

        @Expose
        @Nullable
        public String name;

        @Expose
        @Nullable
        public String artist;

        @Expose
        @Nullable
        public String mbid;

        @Expose
        @Nullable
        public String url;

        @Expose
        @Nullable
        public List<Image> image = new ArrayList<>();

        @Expose
        @Nullable
        public Wiki wiki;

        public static class Image {

            @SerializedName("#text")
            @Expose
            @Nullable
            public String text;
            @Expose
            @Nullable
            public String size;

        }

        public static class Wiki {

            @Expose
            @Nullable
            public String published;
            @Expose
            @Nullable
            public String summary;
            @Expose
            @Nullable
            public String content;

        }


    }

}
