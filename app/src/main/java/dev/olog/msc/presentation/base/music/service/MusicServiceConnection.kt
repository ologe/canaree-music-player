package dev.olog.msc.presentation.base.music.service

import android.support.v4.media.MediaBrowserCompat

class MusicServiceConnection(
        private val activity: MusicGlueActivity

) : MediaBrowserCompat.ConnectionCallback() {

    override fun onConnected() {
        activity.updateConnectionState(MusicServiceConnectionState.CONNECTED)
    }

    override fun onConnectionSuspended() {
        activity.updateConnectionState(MusicServiceConnectionState.FAILED)
    }

    override fun onConnectionFailed() {
        activity.updateConnectionState(MusicServiceConnectionState.FAILED)
    }
}