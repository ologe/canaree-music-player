
package dev.olog.msc.api.last.fm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

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

    /**
     * 
     * @param text
     * @param size
     */
    public Image(String text, String size) {
        super();
        this.text = text;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Image{" +
                "text='" + text + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
