package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackInfo {

    @SerializedName("track")
    @Expose
    public Track track;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrackInfo() {
    }

    public TrackInfo(Track track) {
        super();
        this.track = track;
    }

    @Override
    public String toString() {
        return "TrackInfo{" +
                "track=" + track +
                '}';
    }
}
