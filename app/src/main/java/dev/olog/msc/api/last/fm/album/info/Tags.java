
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tags {

    @SerializedName("tag")
    @Expose
    public List<Tag> tag = new ArrayList<Tag>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Tags() {
    }

    /**
     * 
     * @param tag
     */
    public Tags(List<Tag> tag) {
        super();
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Tags{" +
                "tag=" + tag +
                '}';
    }
}
