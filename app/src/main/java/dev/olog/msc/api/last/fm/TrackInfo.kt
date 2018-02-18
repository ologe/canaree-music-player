package dev.olog.msc.api.last.fm

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.olog.msc.api.last.fm.track.info.Track

data class TrackInfo(
        @SerializedName("track")
        @Expose
        val track: Track
)