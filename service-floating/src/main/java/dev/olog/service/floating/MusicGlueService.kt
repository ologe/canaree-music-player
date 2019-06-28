package dev.olog.service.floating

import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dev.olog.core.dagger.ApplicationContext
import dev.olog.injection.dagger.PerService
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.media.*
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.shared.CustomScope
import dev.olog.shared.extensions.distinctUntilChanged
import dev.olog.shared.extensions.filter
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.extensions.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

@PerService
class MusicGlueService @Inject constructor(
    @ApplicationContext private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle

) : DefaultLifecycleObserver, OnConnectionChanged, CoroutineScope by CustomScope() {

    private val mediaExposer by lazyFast { MediaExposer(context, this) }
    private var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
        mediaExposer.connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController?.unregisterCallback(mediaExposer.callback)
        mediaExposer.disconnect()
        cancel()
    }

    override fun onConnectedSuccess(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        try {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
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

    fun observePlaybackState(): LiveData<PlaybackStateCompat> = mediaExposer.observePlaybackState()
    fun observeMetadata(): LiveData<MediaMetadataCompat> = mediaExposer.observeMetadata()

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

    val animatePlayPauseLiveData: LiveData<Int> = observePlaybackState()
        .filter { it.isPlaying() || it.isPaused() }
        .map { it.state }
        .distinctUntilChanged()

    val onBookmarkChangedLiveData: LiveData<Long> = observePlaybackState()
        .filter { it.isPlaying() || it.isPaused() }
        .map { it.position }

    val onMaxChangedLiveData: LiveData<Long> = observeMetadata()
        .map { it.getDuration() }

}