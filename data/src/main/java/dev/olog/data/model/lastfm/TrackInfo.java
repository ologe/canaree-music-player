package dev.olog.data.model.lastfm;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {

    public Track track;

    public static class Track {

        public String name;

        public String mbid;

        public String listeners;

        public Artist artist;

        public Album album;

        public Wiki wiki;

        public static class Image {

            @SerializedName("#text")
            public String text;

            public String size;
        }

        public class Album {

            public String artist;
            public String title;
            public String mbid;
            public String url;
            public List<Image> image = new ArrayList<>();

        }

        public static class Artist {

            public String name;
            public String mbid;
            public String url;
        }

        public static class Wiki {

            public String summary;
            public String content;

        }


    }

}
