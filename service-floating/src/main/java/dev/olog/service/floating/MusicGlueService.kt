package dev.olog.service.floating

import android.app.Service
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.media.MediaExposer
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.playPause
import dev.olog.media.skipToNext
import dev.olog.media.skipToPrevious
import dev.olog.shared.android.extensions.lazyFast
import dev.olog.media.model.PlayerMetadata
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.shared.android.extensions.lifecycleOwner
import javax.inject.Inject

@ServiceScoped
class MusicGlueService @Inject constructor(
    private val service: Service,
) : DefaultLifecycleObserver, OnConnectionChanged {

    private val mediaExposer by lazyFast {
        MediaExposer(
            context = service,
            lifecycleOwner = service.lifecycleOwner,
            onConnectionChanged = this,
        )
    }
    private var mediaController: MediaControllerCompat? = null

    init {
        service.lifecycleOwner.lifecycle.addObserver(this)
        mediaExposer.connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController?.unregisterCallback(mediaExposer.callback)
        mediaExposer.disconnect()
    }

    override fun onConnectedSuccess(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        try {
            mediaController = MediaControllerCompat(service, mediaBrowser.sessionToken)
            mediaController!!.registerCallback(callback)
            mediaExposer.initialize(mediaController!!)
        } catch (e: RemoteException) {
            e.printStackTrace()
            onConnectedFailed(mediaBrowser, callback)
        }
    }

    override fun onConnectedFailed(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        mediaController?.unregisterCallback(callback)
    }

    fun observePlaybackState(): LiveData<PlayerPlaybackState> = mediaExposer.observePlaybackState()
    fun observeMetadata(): LiveData<PlayerMetadata> = mediaExposer.observeMetadata()

    fun playPause() {
        mediaController?.playPause()
    }

    fun seekTo(progress: Long) {
        mediaController?.transportControls?.seekTo(progress)
    }

    fun skipToNext() {
        mediaController?.skipToNext()
    }

    fun skipToPrevious() {
        mediaController?.skipToPrevious()
    }

}