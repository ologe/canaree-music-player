package dev.olog.service.floating

import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.lib.media.MediaExposer
import dev.olog.lib.media.connection.OnConnectionChanged
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.lib.media.model.PlayerPlaybackState
import dev.olog.lib.media.playPause
import dev.olog.lib.media.skipToNext
import dev.olog.lib.media.skipToPrevious
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ServiceScoped
class MusicGlueService @Inject constructor(
    private val service: LifecycleService,
) : DefaultLifecycleObserver, OnConnectionChanged {

    private val mediaExposer by lazyFast {
        MediaExposer(service, service.lifecycleScope, this)
    }
    private var mediaController: MediaControllerCompat? = null

    init {
        service.lifecycle.addObserver(this)
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

    val metadata: Flow<PlayerMetadata>
        get() = mediaExposer.metadata
    val playbackState: Flow<PlayerPlaybackState>
        get() = mediaExposer.playbackState

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