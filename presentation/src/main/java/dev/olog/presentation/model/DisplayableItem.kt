package dev.olog.presentation.model

import android.content.res.Resources
import dev.olog.presentation.R
import dev.olog.shared.TextUtils

object DisplayableTrack {

    fun subtitle(artist: String, album: String): String {
        return "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"
    }

}

object DisplayableAlbum {

    fun readableSongCount(resources: Resources, size: Int): String {
        if (size <= 0) {
            return ""
        }
        return resources.getQuantityString(R.plurals.common_plurals_song, size, size).lowercase()
    }

}