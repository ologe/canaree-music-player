package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackTopTags {

    @SerializedName("tag")
    @Expose
    public List<TrackTag> tag = null;

}
