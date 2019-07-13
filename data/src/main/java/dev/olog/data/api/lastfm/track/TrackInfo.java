package dev.olog.data.api.lastfm.track;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {

    @Expose
    @Nullable
    public Track track;

    public static class Track {

        @Expose
        @Nullable
        public String name;

        @Expose
        @Nullable
        public String mbid;

        @Expose
        @Nullable
        public String listeners;

        @Expose
        @Nullable
        public Artist artist;

        @Expose
        @Nullable
        public Album album;

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

        public class Album {

            @Expose
            @Nullable
            public String artist;
            @Expose
            @Nullable
            public String title;
            @Expose
            @Nullable
            public String mbid;
            @Expose
            @Nullable
            public String url;
            @Expose
            @Nullable
            public List<Image> image = new ArrayList<>();

        }

        public static class Artist {

            @Expose
            @Nullable
            public String name;
            @Expose
            @Nullable
            public String mbid;
            @Expose
            @Nullable
            public String url;
        }

        public static class Wiki {

            @Expose
            @Nullable
            public String summary;
            @Expose
            @Nullable
            public String content;

        }


    }

}
