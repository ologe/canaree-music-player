
package dev.olog.data.api.lastfm.artist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArtistInfo {

    public Artist artist;

    public static class Artist {

        public String name;

        public String mbid;

        public String url;

        public List<Image> image = new ArrayList<>();

        public Bio bio;

        public static class Image {

            @SerializedName("#text")
            public String text;
            public String size;
        }

        public static class Bio {

            public Links links;
            public String published;
            public String summary;
            public String content;

            public static class Links {

                public Link link;

                public static class Link {

                    @SerializedName("#text")
                    public String text;
                    public String rel;
                    public String href;

                }

            }


        }

    }


}
