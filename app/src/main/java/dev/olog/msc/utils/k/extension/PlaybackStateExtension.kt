package dev.olog.msc.utils.k.extension

import android.support.v4.media.session.PlaybackStateCompat

fun PlaybackStateCompat.isPlaying(): Boolean {
    return state == PlaybackStateCompat.STATE_PLAYING
}

fun PlaybackStateCompat.isPaused(): Boolean {
    return state == PlaybackStateCompat.STATE_PAUSED
}