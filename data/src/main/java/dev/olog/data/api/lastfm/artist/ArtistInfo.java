
package dev.olog.data.api.lastfm.artist;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArtistInfo {

    @Expose
    @Nullable
    public Artist artist;

    public static class Artist {

        @Expose
        @Nullable
        public String name;

        @Expose
        @Nullable
        public String mbid;

        @Expose
        @Nullable
        public String url;

        @Expose
        @Nullable
        public List<Image> image = new ArrayList<>();

        @Expose
        @Nullable
        public Bio bio;

        public static class Image {

            @SerializedName("#text")
            @Expose
            @Nullable
            public String text;
            @Expose
            @Nullable
            public String size;
        }

        public static class Bio {

            @Expose
            @Nullable
            public Links links;
            @Expose
            @Nullable
            public String published;
            @Expose
            @Nullable
            public String summary;
            @Expose
            @Nullable
            public String content;

            public static class Links {

                @Expose
                @Nullable
                public Link link;

                public static class Link {

                    @SerializedName("#text")
                    @Expose
                    @Nullable
                    public String text;
                    @Expose
                    @Nullable
                    public String rel;
                    @Expose
                    @Nullable
                    public String href;

                }

            }


        }

    }


}
