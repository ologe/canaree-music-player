package dev.olog.lib.media.model

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerRepeatMode {
    NONE,
    ONE,
    ALL;

    companion object {
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

    fun cycled() = when (this) {
        NONE -> ALL
        ALL -> ONE
        ONE -> NONE
    }

    @PlaybackStateCompat.RepeatMode
    fun toPlatform(): Int = when (this) {
        NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
        ONE -> PlaybackStateCompat.REPEAT_MODE_ONE
        ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
    }

}