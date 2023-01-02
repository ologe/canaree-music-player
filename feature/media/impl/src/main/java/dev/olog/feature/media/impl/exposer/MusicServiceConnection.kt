package dev.olog.feature.media.impl.exposer

import android.support.v4.media.MediaBrowserCompat
import dev.olog.feature.media.api.connection.IMediaConnectionCallback
import dev.olog.feature.media.api.connection.MusicServiceConnectionState

internal class MusicServiceConnection(
    private val connectionCallback: IMediaConnectionCallback

) : MediaBrowserCompat.ConnectionCallback() {

    override fun onConnected() {
        connectionCallback.onConnectionStateChanged(MusicServiceConnectionState.CONNECTED)
    }

    override fun onConnectionSuspended() {
        connectionCallback.onConnectionStateChanged(MusicServiceConnectionState.FAILED)
    }

    override fun onConnectionFailed() {
        connectionCallback.onConnectionStateChanged(MusicServiceConnectionState.FAILED)
    }
}