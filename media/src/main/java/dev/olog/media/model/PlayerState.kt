package dev.olog.media.model

import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerState {
    PLAYING,
    PAUSED,
    SKIP_TO_NEXT,
    SKIP_TO_PREVIOUS;

    companion object {
        @JvmStatic
        fun of(@PlaybackStateCompat.State state: Int): PlayerState = when (state) {
            PlaybackStateCompat.STATE_PLAYING -> PLAYING
            PlaybackStateCompat.STATE_PAUSED -> PAUSED
            PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> SKIP_TO_NEXT
            PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> SKIP_TO_PREVIOUS
            // not handled
            PlaybackStateCompat.STATE_NONE,
            PlaybackStateCompat.STATE_STOPPED,
            PlaybackStateCompat.STATE_FAST_FORWARDING,
            PlaybackStateCompat.STATE_REWINDING,
            PlaybackStateCompat.STATE_BUFFERING,
            PlaybackStateCompat.STATE_ERROR,
            PlaybackStateCompat.STATE_CONNECTING,
            PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM -> throw IllegalArgumentException("state not handled $state")
            // kotlin compiler wants an else branch
            else -> throw IllegalArgumentException("state not handled $state")
        }
    }
}