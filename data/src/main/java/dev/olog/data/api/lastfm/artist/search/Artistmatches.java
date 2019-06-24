
package dev.olog.data.api.lastfm.artist.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Artistmatches {

    @SerializedName("artist")
    @Expose
    public List<Artist> artist = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Artistmatches() {
    }

    public Artistmatches(List<Artist> artist) {
        super();
        this.artist = artist;
    }

    public static class Artist {

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("mbid")
        @Expose
        public String mbid;

        @SerializedName("image")
        @Expose
        public List<Image> image = new ArrayList<>();

        /**
         * No args constructor for use in serialization
         *
         */
        public Artist() {
        }

        public Artist(String name, String mbid, List<Image> image) {
            super();
            this.name = name;
            this.mbid = mbid;
            this.image = image;
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

    }

}
