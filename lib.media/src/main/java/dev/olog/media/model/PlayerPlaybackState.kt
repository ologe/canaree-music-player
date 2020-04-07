package dev.olog.media.model

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import dev.olog.media.model.PlayerState.*

class PlayerPlaybackState(
    private val stateCompat: PlaybackStateCompat
) {

    val state : PlayerState
        get() = PlayerState.of(stateCompat.state)

    val bookmark: Int
        get() = stateCompat.extractBookmark()

    val playbackSpeed : Float
        get() = stateCompat.playbackSpeed

    val isPlaying: Boolean
        get() = state == PLAYING

    val isPaused : Boolean
        get() = state == PAUSED

    val isSkipTo: Boolean
        get() = state == SKIP_TO_NEXT || state == SKIP_TO_PREVIOUS

    val isPlayOrPause: Boolean
        get() = isPlaying || isPaused

    private fun PlaybackStateCompat.extractBookmark(): Int {
        var bookmark = this.position

        if (this.state == STATE_PLAYING) {
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