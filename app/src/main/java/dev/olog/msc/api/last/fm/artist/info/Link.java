
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link {

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

    /**
     * 
     * @param text
     * @param rel
     * @param href
     */
    public Link(String text, String rel, String href) {
        super();
        this.text = text;
        this.rel = rel;
        this.href = href;
    }

    @Override
    public String toString() {
        return "Link{" +
                "text='" + text + '\'' +
                ", rel='" + rel + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
