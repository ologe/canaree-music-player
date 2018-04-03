
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArtistInfo {

    @SerializedName("artist")
    @Expose
    public Artist artist;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ArtistInfo() {
    }

    public ArtistInfo(Artist artist) {
        super();
        this.artist = artist;
    }

    public static class Artist {

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("mbid")
        @Expose
        public String mbid;

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("image")
        @Expose
        public List<Image> image = new ArrayList<>();

        @SerializedName("bio")
        @Expose
        public Bio bio;

        /**
         * No args constructor for use in serialization
         *
         */
        public Artist() {
        }

        public Artist(String name, String mbid, String url, List<Image> image, Bio bio) {
            super();
            this.name = name;
            this.mbid = mbid;
            this.url = url;
            this.image = image;
            this.bio = bio;
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

        public static class Bio {

            @SerializedName("links")
            @Expose
            public Links links;
            @SerializedName("published")
            @Expose
            public String published;
            @SerializedName("summary")
            @Expose
            public String summary;
            @SerializedName("content")
            @Expose
            public String content;

            /**
             * No args constructor for use in serialization
             *
             */
            public Bio() {
            }

            public Bio(Links links, String published, String summary, String content) {
                super();
                this.links = links;
                this.published = published;
                this.summary = summary;
                this.content = content;
            }

            public static class Links {

                @SerializedName("link")
                @Expose
                public Link link;

                /**
                 * No args constructor for use in serialization
                 *
                 */
                public Links() {
                }

                public Links(Link link) {
                    super();
                    this.link = link;
                }

                public static class Link {

                    @SerializedName("#text")
                    @Expose
                    public String text;
                    @SerializedName("rel")
                    @Expose
                    public String rel;
                    @SerializedName("href")
                    @Expose
                    public String href;

                    /**
                     * No args constructor for use in serialization
                     *
                     */
                    public Link() {
                    }

                    public Link(String text, String rel, String href) {
                        super();
                        this.text = text;
                        this.rel = rel;
                        this.href = href;
                    }

                }

            }


        }

    }


}
