
package dev.olog.data.api.lastfm.track;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackSearch {

    @Expose
    @Nullable
    public Results results;

    public static class Results {

        @Expose
        @Nullable
        public Trackmatches trackmatches;

        public static class Trackmatches {

            @Expose
            @Nullable
            public List<Track> track = new ArrayList<Track>();

            public static class Track {

                @Expose
                @Nullable
                public String name;

                @Expose
                @Nullable
                public String artist;

                @Expose
                @Nullable
                public String url;

                @Expose
                @Nullable
                public List<Image> image = new ArrayList<Image>();

                @Expose
                @Nullable
                public String mbid;

                public static class Image {

                    @SerializedName("#text")
                    @Expose
                    @Nullable
                    public String text;
                    @Expose
                    @Nullable
                    public String size;

                }

            }

        }

    }

}
