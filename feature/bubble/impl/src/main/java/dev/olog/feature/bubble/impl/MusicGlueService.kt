package dev.olog.feature.bubble.impl

import android.app.Service
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.api.FeatureMediaNavigator
import dev.olog.feature.media.api.MediaExposer
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.extension.playPause
import dev.olog.feature.media.api.extension.skipToNext
import dev.olog.feature.media.api.extension.skipToPrevious
import dev.olog.shared.lazyFast
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.platform.extension.lifecycleOwner
import dev.olog.platform.permission.PermissionManager
import javax.inject.Inject

@ServiceScoped
class MusicGlueService @Inject constructor(
    private val service: Service,
    private val permissionManager: PermissionManager,
    private val featureMediaNavigator: FeatureMediaNavigator,
) : DefaultLifecycleObserver, OnConnectionChanged {

    private val mediaExposer by lazyFast {
        MediaExposer(
            context = service,
            componentName = featureMediaNavigator.createComponentName(),
            lifecycleOwner = service.lifecycleOwner,
            onConnectionChanged = this,
            permissionManager = permissionManager,
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