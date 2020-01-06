
package dev.olog.data.model.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackSearch {

    public Results results;

    public static class Results {

        public Trackmatches trackmatches;

        public static class Trackmatches {

            public List<Track> track = new ArrayList<Track>();

            public static class Track {

                public String name;

                public String artist;

                public String url;

                public List<Image> image = new ArrayList<Image>();

                public String mbid;

                public static class Image {

                    @SerializedName("#text")
                    public String text;
                    public String size;

                }

            }

        }

    }

}
