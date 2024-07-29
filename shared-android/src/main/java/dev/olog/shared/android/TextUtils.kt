package dev.olog.shared.android

import android.content.res.Resources
import dev.olog.shared.TextUtils

object TextUtils {

    const val MIDDLE_DOT = TextUtils.MIDDLE_DOT
    const val MIDDLE_DOT_SPACED = TextUtils.MIDDLE_DOT_SPACED

    fun subtitle(artist: String, album: String): String {
        return "$artist${MIDDLE_DOT_SPACED}$album"
    }

    fun readableSongCount(resources: Resources, size: Int): String {
        if (size <= 0) {
            return ""
        }
        return resources.getQuantityString(R.plurals.common_plurals_song, size, size).toLowerCase()
    }

}