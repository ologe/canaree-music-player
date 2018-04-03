package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {

    @SerializedName("track")
    @Expose
    public Track track;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackInfo() {
    }

    public TrackInfo(Track track) {
        super();
        this.track = track;
    }

    public static class Track {

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("mbid")
        @Expose
        public String mbid;

        @SerializedName("listeners")
        @Expose
        public String listeners;

        @SerializedName("artist")
        @Expose
        public Artist artist;

        @SerializedName("album")
        @Expose
        public Album album;

        @SerializedName("wiki")
        @Expose
        public Wiki wiki;

        /**
         * No args constructor for use in serialization
         *
         */
        public Track() {
        }

        public Track(String name, String mbid, Artist artist, Album album, Wiki wiki) {
            super();
            this.name = name;
            this.mbid = mbid;
            this.artist = artist;
            this.album = album;
            this.wiki = wiki;
        }

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;

            @SerializedName("size")
            @Expose
            public String size;

            /**
             * No args constructor for use in serialization
             *
             */
            public Image() {
            }

            public Image(String text, String size) {
                super();
                this.text = text;
                this.size = size;
            }
        }

        public class Album {

            @SerializedName("artist")
            @Expose
            public String artist;
            @SerializedName("title")
            @Expose
            public String title;
            @SerializedName("mbid")
            @Expose
            public String mbid;
            @SerializedName("url")
            @Expose
            public String url;
            @SerializedName("image")
            @Expose
            public List<Image> image = new ArrayList<>();

            /**
             * No args constructor for use in serialization
             *
             */
            public Album() {
            }

            public Album(String artist, String title, String mbid, String url, List<Image> image) {
                super();
                this.artist = artist;
                this.title = title;
                this.mbid = mbid;
                this.url = url;
                this.image = image;
            }

        }

        public static class Artist {

            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("mbid")
            @Expose
            public String mbid;
            @SerializedName("url")
            @Expose
            public String url;

            /**
             * No args constructor for use in serialization
             *
             */
            public Artist() {
            }

            public Artist(String name, String mbid, String url) {
                super();
                this.name = name;
                this.mbid = mbid;
                this.url = url;
            }

        }

        public static class Wiki {

            @SerializedName("summary")
            @Expose
            public String summary;
            @SerializedName("content")
            @Expose
            public String content;

            /**
             * No args constructor for use in serialization
             *
             */
            public Wiki() {
            }

            public Wiki(String summary, String content) {
                super();
                this.summary = summary;
                this.content = content;
            }

        }


    }

}
