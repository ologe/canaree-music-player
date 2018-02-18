
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bio {

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

    /**
     * 
     * @param content
     * @param summary
     * @param links
     * @param published
     */
    public Bio(Links links, String published, String summary, String content) {
        super();
        this.links = links;
        this.published = published;
        this.summary = summary;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Bio{" +
                "links=" + links +
                ", published='" + published + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
