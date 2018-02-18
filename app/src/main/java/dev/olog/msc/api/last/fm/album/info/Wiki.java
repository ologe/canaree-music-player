
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wiki {

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
    public Wiki() {
    }

    /**
     * 
     * @param content
     * @param summary
     * @param published
     */
    public Wiki(String published, String summary, String content) {
        super();
        this.published = published;
        this.summary = summary;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Wiki{" +
                "published='" + published + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
