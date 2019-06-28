package dev.olog.service.music.interfaces

import android.support.v4.media.session.PlaybackStateCompat

interface PlayerLifecycle {

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPrepare(entity: dev.olog.service.music.model.MediaEntity) {}
        fun onMetadataChanged(entity: dev.olog.service.music.model.MediaEntity) {}
        fun onStateChanged(state: PlaybackStateCompat){}
        fun onSeek(where: Long){}
    }

}