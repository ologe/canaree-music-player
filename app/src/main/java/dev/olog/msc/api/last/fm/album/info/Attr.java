
package dev.olog.msc.api.last.fm.album.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attr {

    @SerializedName("rank")
    @Expose
    public String rank;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Attr() {
    }

    /**
     * 
     * @param rank
     */
    public Attr(String rank) {
        super();
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Attr{" +
                "rank='" + rank + '\'' +
                '}';
    }
}
