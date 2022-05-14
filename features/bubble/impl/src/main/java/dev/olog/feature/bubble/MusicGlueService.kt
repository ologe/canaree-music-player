package dev.olog.feature.bubble

import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.api.MediaExposer
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.extensions.playPause
import dev.olog.feature.media.api.extensions.skipToNext
import dev.olog.feature.media.api.extensions.skipToPrevious
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.platform.ServiceLifecycle
import dev.olog.shared.extension.lazyFast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ServiceScoped
class MusicGlueService @Inject constructor(
    @ApplicationContext private val context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    private val featureMediaNavigator: FeatureMediaNavigator,
) : DefaultLifecycleObserver, OnConnectionChanged {

    private val mediaExposer by lazyFast {
        MediaExposer(
            context = context,
            onConnectionChanged = this,
            scope = lifecycle.coroutineScope,
            componentName = featureMediaNavigator.serviceComponent(),
        )
    }
    private var mediaController: MediaControllerCompat? = null

    init {
        lifecycle.addObserver(this)
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