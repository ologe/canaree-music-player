
package dev.olog.msc.api.last.fm.track.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Trackmatches {

    @SerializedName("track")
    @Expose
    public List<Track> track = new ArrayList<Track>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Trackmatches() {
    }

    /**
     * 
     * @param track
     */
    public Trackmatches(List<Track> track) {
        super();
        this.track = track;
    }

    public List<Track> getTrack() {
        return track;
    }
}
