package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackInfo {

    @SerializedName("track")
    @Expose
    public Track track;

}