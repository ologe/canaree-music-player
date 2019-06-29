
package dev.olog.data.api.lastfm.track;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackSearch {

    @Expose
    public Results results;

    public static class Results {

        @Expose
        public Trackmatches trackmatches;

        public static class Trackmatches {

            @Expose
            public List<Track> track = new ArrayList<Track>();

            public static class Track {

                @Expose
                public String name;

                @Expose
                public String artist;

                @Expose
                public String url;

                @Expose
                public List<Image> image = new ArrayList<Image>();

                @Expose
                public String mbid;

                public static class Image {

                    @SerializedName("#text")
                    @Expose
                    public String text;
                    @Expose
                    public String size;

                }

            }

        }

    }

}
