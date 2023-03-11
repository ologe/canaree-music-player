package dev.olog.feature.media.api.model

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat

enum class PlayerState {
    PLAYING,
    PAUSED,
    SKIP_TO_NEXT,
    SKIP_TO_PREVIOUS;

    companion object {
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

class PlayerPlaybackState(private val stateCompat: PlaybackStateCompat) {

    val state = PlayerState.of(stateCompat.state)
    val bookmark = stateCompat.extractBookmark()
    val playbackSpeed = stateCompat.playbackSpeed

    val isPlaying = state == PlayerState.PLAYING
    val isPaused = state == PlayerState.PAUSED

    val isSkipTo = state == PlayerState.SKIP_TO_NEXT || state == PlayerState.SKIP_TO_PREVIOUS

    val isPlayOrPause = isPlaying || isPaused

    private fun PlaybackStateCompat.extractBookmark(): Int {
        var bookmark = this.position

        if (this.state == PlaybackStateCompat.STATE_PLAYING) {
            val timeDelta = SystemClock.elapsedRealtime() - this.lastPositionUpdateTime
            bookmark += (timeDelta * this.playbackSpeed).toLong()
        }
        return bookmark.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is PlayerPlaybackState) {
            return false
        }
        if (this === other) {
            return true
        }

        return this.state == other.state &&
                this.bookmark == other.bookmark &&
                this.playbackSpeed == other.playbackSpeed
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + bookmark
        result = 31 * result + playbackSpeed.hashCode()
        return result
    }


}