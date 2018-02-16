package dev.olog.msc.floating.window.service.music.service

import android.support.v4.media.MediaBrowserCompat
import dev.olog.msc.floating.window.service.service.MusicServiceBinder
import dev.olog.msc.presentation.base.music.service.MusicServiceConnectionState

class FloatingMusicConnection(
        private val binder: MusicServiceBinder

) : MediaBrowserCompat.ConnectionCallback() {

    override fun onConnected() {
        binder.updateConnectionState(MusicServiceConnectionState.CONNECTED)
    }

    override fun onConnectionSuspended() {
        binder.updateConnectionState(MusicServiceConnectionState.FAILED)
    }

    override fun onConnectionFailed() {
        binder.updateConnectionState(MusicServiceConnectionState.FAILED)
    }
}