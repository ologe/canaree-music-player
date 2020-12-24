package dev.olog.lib.media.model

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerShuffleMode {
    DISABLED,
    ENABLED;

    companion object {
        fun of(@PlaybackStateCompat.ShuffleMode shuffleMode: Int) = when (shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> DISABLED
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> ENABLED

            // not handled
            PlaybackStateCompat.SHUFFLE_MODE_INVALID,
            PlaybackStateCompat.SHUFFLE_MODE_GROUP -> DISABLED
            // kotlin compiler wants an else branch
            else -> DISABLED
        }
    }

    fun cycled() = when (this) {
        DISABLED -> ENABLED
        ENABLED -> DISABLED
    }

    @PlaybackStateCompat.ShuffleMode
    fun toPlatform(): Int = when (this) {
        DISABLED -> PlaybackStateCompat.SHUFFLE_MODE_NONE
        ENABLED -> PlaybackStateCompat.SHUFFLE_MODE_ALL
    }

}