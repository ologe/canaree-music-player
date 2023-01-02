package dev.olog.feature.media.api.model

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerShuffleMode {
    NOT_SET,
    DISABLED,
    ENABLED;

    companion object {
        @JvmStatic
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
}