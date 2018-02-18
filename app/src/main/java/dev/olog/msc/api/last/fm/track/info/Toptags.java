package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Toptags {

    @SerializedName("tag")
    @Expose
    public List<Tag> tag = new ArrayList<Tag>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Toptags() {
    }

    /**
     * 
     * @param tag
     */
    public Toptags(List<Tag> tag) {
        super();
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Toptags{" +
                "tag=" + tag +
                '}';
    }
}
