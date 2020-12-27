package dev.olog.shared.android

import android.content.res.Resources
import dev.olog.shared.TextUtils

object DisplayableItemUtils {

    fun readableSongCount(resources: Resources, size: Int): String {
        if (size <= 0) {
            return ""
        }
        return resources.getQuantityString(R.plurals.common_plurals_song, size, size)
            .toLowerCase()
    }

    fun trackSubtitle(artist: String, album: String): String {
        return "$artist${TextUtils.MIDDLE_DOT_SPACED}$album"
    }

}