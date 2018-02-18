
package dev.olog.msc.api.last.fm.artist.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("listeners")
    @Expose
    public String listeners;
    @SerializedName("playcount")
    @Expose
    public String playcount;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Stats() {
    }

    /**
     * 
     * @param listeners
     * @param playcount
     */
    public Stats(String listeners, String playcount) {
        super();
        this.listeners = listeners;
        this.playcount = playcount;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "listeners='" + listeners + '\'' +
                ", playcount='" + playcount + '\'' +
                '}';
    }
}
