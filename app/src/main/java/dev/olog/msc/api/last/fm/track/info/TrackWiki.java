package dev.olog.msc.api.last.fm.track.info;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackWiki {

    @SerializedName("published")
    @Expose
    public String published;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("content")
    @Expose
    public String content;

}
