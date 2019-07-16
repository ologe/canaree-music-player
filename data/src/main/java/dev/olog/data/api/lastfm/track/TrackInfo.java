package dev.olog.data.api.lastfm.track;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {

    @Expose
    public Track track;

    public static class Track {

        @Expose
        public String name;

        @Expose
        public String mbid;

        @Expose
        public String listeners;

        @Expose
        public Artist artist;

        @Expose
        public Album album;

        @Expose
        public Wiki wiki;

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;

            @Expose
            public String size;
        }

        public class Album {

            @Expose
            public String artist;
            @Expose
            public String title;
            @Expose
            public String mbid;
            @Expose
            public String url;
            @Expose
            public List<Image> image = new ArrayList<>();

        }

        public static class Artist {

            @Expose
            public String name;
            @Expose
            public String mbid;
            @Expose
            public String url;
        }

        public static class Wiki {

            @Expose
            public String summary;
            @Expose
            public String content;

        }


    }

}
