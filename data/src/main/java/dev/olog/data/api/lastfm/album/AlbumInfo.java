
package dev.olog.data.api.lastfm.album;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AlbumInfo {

    public Album album;

    public static class Album {

        public String name;

        public String artist;

        public String mbid;

        public String url;

        public List<Image> image = new ArrayList<>();

        public Wiki wiki;

        public static class Image {

            @SerializedName("#text")
            public String text;
            public String size;

        }

        public static class Wiki {

            public String published;
            public String summary;
            public String content;

        }


    }

}
