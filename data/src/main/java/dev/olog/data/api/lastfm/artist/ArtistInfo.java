
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArtistInfo {

    @Expose
    public Artist artist;

    public static class Artist {

        @Expose
        public String name;

        @Expose
        public String mbid;

        @Expose
        public String url;

        @Expose
        public List<Image> image = new ArrayList<>();

        @Expose
        public Bio bio;

        public static class Image {

            @SerializedName("#text")
            @Expose
            public String text;
            @Expose
            public String size;
        }

        public static class Bio {

            @Expose
            public Links links;
            @Expose
            public String published;
            @Expose
            public String summary;
            @Expose
            public String content;

            public static class Links {

                @Expose
                public Link link;

                public static class Link {

                    @SerializedName("#text")
                    @Expose
                    public String text;
                    @Expose
                    public String rel;
                    @Expose
                    public String href;

                }

            }


        }

    }


}
