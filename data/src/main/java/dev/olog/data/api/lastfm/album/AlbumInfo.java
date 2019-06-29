
package dev.olog.data.api.lastfm.album;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo {

    @Expose
    public Album album;

    public static class Album {

        @Expose
        public String name;

        @Expose
        public String artist;

        @Expose
        public String mbid;

        @Expose
        public String url;

        @Expose
        public List<Image> image = new ArrayList<>();

        @Expose
        public Wiki wiki;

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;
            @Expose
            public String size;

        }

        public static class Wiki {

            @Expose
            public String published;
            @Expose
            public String summary;
            @Expose
            public String content;

        }


    }

}
