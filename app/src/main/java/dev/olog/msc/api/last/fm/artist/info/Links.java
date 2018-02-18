
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links {

    @SerializedName("link")
    @Expose
    public Link link;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Links() {
    }

    /**
     * 
     * @param link
     */
    public Links(Link link) {
        super();
        this.link = link;
    }

    @Override
    public String toString() {
        return "Links{" +
                "link=" + link +
                '}';
    }
}
