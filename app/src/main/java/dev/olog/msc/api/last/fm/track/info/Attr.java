package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attr {

    @SerializedName("position")
    @Expose
    public String position;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Attr() {
    }

    /**
     * 
     * @param position
     */
    public Attr(String position) {
        super();
        this.position = position;
    }

    @Override
    public String toString() {
        return "Attr{" +
                "position='" + position + '\'' +
                '}';
    }
}
