package dev.olog.media.model

import android.support.v4.media.session.PlaybackStateCompat.*
import dev.olog.shared.throwNotHandled

enum class PlayerState {
    PLAYING,
    PAUSED,
    SKIP_TO_NEXT,
    SKIP_TO_PREVIOUS;

    companion object {
        @JvmStatic
        fun of(@State state: Int): PlayerState = when (state) {
            STATE_PLAYING -> PLAYING
            STATE_PAUSED -> PAUSED
            STATE_SKIPPING_TO_NEXT -> SKIP_TO_NEXT
            STATE_SKIPPING_TO_PREVIOUS -> SKIP_TO_PREVIOUS

            // not handled
            STATE_NONE,
            STATE_STOPPED,
            STATE_FAST_FORWARDING,
            STATE_REWINDING,
            STATE_BUFFERING,
            STATE_ERROR,
            STATE_CONNECTING,
            STATE_SKIPPING_TO_QUEUE_ITEM -> throwNotHandled(state)
            else -> throwNotHandled(state)
        }
    }
}