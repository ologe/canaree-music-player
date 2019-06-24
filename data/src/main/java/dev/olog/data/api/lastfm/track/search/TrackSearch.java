
package dev.olog.data.api.lastfm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackSearch {

    @SerializedName("results")
    @Expose
    public Results results;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackSearch() {
    }

    public TrackSearch(Results results) {
        super();
        this.results = results;
    }

    public static class Results {

        @SerializedName("trackmatches")
        @Expose
        public Trackmatches trackmatches;

        /**
         * No args constructor for use in serialization
         *
         */
        public Results() {
        }

        public Results(Trackmatches trackmatches) {
            super();
            this.trackmatches = trackmatches;
        }

        public static class Trackmatches {

            @SerializedName("track")
            @Expose
            public List<Track> track = new ArrayList<Track>();

            /**
             * No args constructor for use in serialization
             *
             */
            public Trackmatches() {
            }

            public Trackmatches(List<Track> track) {
                super();
                this.track = track;
            }

            public List<Track> getTrack() {
                return track;
            }


            public static class Track {

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
                public List<Image> image = new ArrayList<Image>();

                @SerializedName("mbid")
                @Expose
                public String mbid;

                /**
                 * No args constructor for use in serialization
                 *
                 */
                public Track() {
                }

                public Track(String name, String artist, String url, List<Image> image, String mbid) {
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

    }

}
