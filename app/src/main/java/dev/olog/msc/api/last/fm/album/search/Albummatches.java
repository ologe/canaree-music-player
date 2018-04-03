
package dev.olog.msc.api.last.fm.album.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albummatches {

    @SerializedName("album")
    @Expose
    public List<Album> album = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Albummatches() {
    }

    public Albummatches(List<Album> album) {
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

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("image")
        @Expose
        public List<Image> image = new ArrayList<>();

        @SerializedName("mbid")
        @Expose
        public String mbid;

        /**
         * No args constructor for use in serialization
         *
         */
        public Album() {
        }

        public Album(String name, String artist, String url, List<Image> image, String mbid) {
            super();
            this.name = name;
            this.artist = artist;
            this.url = url;
            this.image = image;
            this.mbid = mbid;
        }

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;

            /**
             * No args constructor for use in serialization
             *
             */
            public Image() {
            }

            public Image(String text) {
                super();
                this.text = text;
            }
        }


    }


}
