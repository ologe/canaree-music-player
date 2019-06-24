
package dev.olog.data.api.lastfm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo {

    @SerializedName("album")
    @Expose
    public Album album;

    /**
     * No args constructor for use in serialization
     *
     */
    public AlbumInfo() {
    }

    public AlbumInfo(Album album) {
        super();
        this.album = album;
    }

    public static class Album {

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("artist")
        @Expose
        public String artist;

        @SerializedName("mbid")
        @Expose
        public String mbid;

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("image")
        @Expose
        public List<Image> image = new ArrayList<>();

        @SerializedName("wiki")
        @Expose
        public Wiki wiki;

        /**
         * No args constructor for use in serialization
         *
         */
        public Album() {
        }

        public Album(String name, String artist, String mbid,
                     String url, List<Image> image, Wiki wiki) {
            super();
            this.name = name;
            this.artist = artist;
            this.mbid = mbid;
            this.url = url;
            this.image = image;
            this.wiki = wiki;
        }

//        public static class Artist {
//
//            @SerializedName("name")
//            @Expose
//            public String name;
//
//            @SerializedName("mbid")
//            @Expose
//            public String mbid;
//
//            @SerializedName("url")
//            @Expose
//            public String url;
//
//            /**
//             * No args constructor for use in serialization
//             *
//             */
//            public Artist() {
//            }
//
//
//            public Artist(String name, String mbid, String url) {
//                super();
//                this.name = name;
//                this.mbid = mbid;
//                this.url = url;
//            }
//        }


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

        public static class Wiki {

            @SerializedName("published")
            @Expose
            public String published;
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

            public Wiki(String published, String summary, String content) {
                super();
                this.published = published;
                this.summary = summary;
                this.content = content;
            }

        }


    }

}
