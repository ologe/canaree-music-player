package dev.olog.msc.utils.k.extension

import android.os.SystemClock
import android.support.v4.media.session.PlaybackStateCompat

fun PlaybackStateCompat.isPlaying(): Boolean {
    return state == PlaybackStateCompat.STATE_PLAYING
}

fun PlaybackStateCompat.isPaused(): Boolean {
    return state == PlaybackStateCompat.STATE_PAUSED
}

fun PlaybackStateCompat.extractBookmark(): Int {
    var bookmark = this.position

    if (this.state == PlaybackStateCompat.STATE_PLAYING){
        val timeDelta = SystemClock.elapsedRealtime() - this.lastPositionUpdateTime
        bookmark += (timeDelta * this.playbackSpeed).toLong()
    }
    return bookmark.toInt()
}