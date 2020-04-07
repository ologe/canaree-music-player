package dev.olog.lib.media.connection

import android.support.v4.media.MediaBrowserCompat
import dev.olog.lib.media.connection.MusicServiceConnectionState.CONNECTED
import dev.olog.lib.media.connection.MusicServiceConnectionState.FAILED
import timber.log.Timber

internal class MusicServiceConnection(
    private val connectionCallback: IMediaConnectionCallback

) : MediaBrowserCompat.ConnectionCallback() {

    companion object {
        private const val TAG = "MusicServiceConnection"
    }

    override fun onConnected() {
        Timber.v("$TAG: onConnected")
        connectionCallback.onConnectionStateChanged(CONNECTED)
    }

    override fun onConnectionSuspended() {
        Timber.w("$TAG: onConnectionSuspended")
        connectionCallback.onConnectionStateChanged(FAILED)
    }

    override fun onConnectionFailed() {
        Timber.w("$TAG: onConnectionFailed")
        connectionCallback.onConnectionStateChanged(FAILED)
    }
}