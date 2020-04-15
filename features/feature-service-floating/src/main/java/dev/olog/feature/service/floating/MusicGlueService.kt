package dev.olog.feature.service.floating

import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.dagger.FeatureScope
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.lib.media.MediaExposer
import dev.olog.lib.media.connection.OnConnectionChanged
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.lib.media.model.PlayerPlaybackState
import dev.olog.lib.media.playPause
import dev.olog.lib.media.skipToNext
import dev.olog.lib.media.skipToPrevious
import dev.olog.navigation.screens.Services
import dev.olog.navigation.screens.ServicesMap
import dev.olog.shared.android.Permissions
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@FeatureScope
class MusicGlueService @Inject constructor(
    private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    private val schedulers: Schedulers,
    private val services: ServicesMap

) : DefaultLifecycleObserver, OnConnectionChanged {

    private val mediaExposer by lazyFast {
        MediaExposer(context, this, schedulers, services[Services.MUSIC]!!) {
            Permissions.canReadStorage(context)
        }
    }
    private var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
        mediaExposer.connect()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaController?.unregisterCallback(mediaExposer.callback)
        mediaExposer.disconnect()
        mediaExposer.dispose()
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
            Timber.e(e)
            onConnectedFailed(mediaBrowser, callback)
        }
    }

    override fun onConnectedFailed(
        mediaBrowser: MediaBrowserCompat,
        callback: MediaControllerCompat.Callback
    ) {
        mediaController?.unregisterCallback(callback)
    }

    fun observePlaybackState(): Flow<PlayerPlaybackState> = mediaExposer.observePlaybackState()
    fun observeMetadata(): Flow<PlayerMetadata> = mediaExposer.observeMetadata()

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