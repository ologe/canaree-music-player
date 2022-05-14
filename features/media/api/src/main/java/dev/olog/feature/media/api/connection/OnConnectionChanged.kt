package dev.olog.feature.media.api.connection

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat

interface OnConnectionChanged {
    fun onConnectedSuccess(mediaBrowser: MediaBrowserCompat, callback: MediaControllerCompat.Callback)
    fun onConnectedFailed(mediaBrowser: MediaBrowserCompat, callback: MediaControllerCompat.Callback)
}