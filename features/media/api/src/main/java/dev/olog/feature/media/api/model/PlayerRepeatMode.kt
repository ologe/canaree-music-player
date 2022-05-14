package dev.olog.feature.media.api.model

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerRepeatMode {
    NOT_SET,
    NONE,
    ONE,
    ALL;

    companion object {
        @JvmStatic
        fun of(@PlaybackStateCompat.RepeatMode repeatMode: Int) = when (repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> NONE
            PlaybackStateCompat.REPEAT_MODE_ONE -> ONE
            PlaybackStateCompat.REPEAT_MODE_ALL -> ALL

            // not handled
            PlaybackStateCompat.REPEAT_MODE_INVALID,
            PlaybackStateCompat.REPEAT_MODE_GROUP -> NONE
            // kotlin compiler wants an else branch
            else -> NONE
        }
    }
}