
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Streamable {

    @SerializedName("#text")
    @Expose
    public String text;
    @SerializedName("fulltrack")
    @Expose
    public String fulltrack;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Streamable() {
    }

    /**
     * 
     * @param text
     * @param fulltrack
     */
    public Streamable(String text, String fulltrack) {
        super();
        this.text = text;
        this.fulltrack = fulltrack;
    }

    @Override
    public String toString() {
        return "Streamable{" +
                "text='" + text + '\'' +
                ", fulltrack='" + fulltrack + '\'' +
                '}';
    }
}
