package dev.olog.feature.media.api

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.feature.media.api.connection.IMediaConnectionCallback
import dev.olog.feature.media.api.connection.MusicServiceConnection
import dev.olog.feature.media.api.connection.MusicServiceConnectionState
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.controller.IMediaControllerCallback
import dev.olog.feature.media.api.controller.MediaControllerCallback
import dev.olog.feature.media.api.model.PlayerItem
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerRepeatMode
import dev.olog.feature.media.api.model.PlayerShuffleMode
import dev.olog.shared.distinctUntilChanged
import dev.olog.shared.lazyFast
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MediaExposer(
    private val context: Context,
    private val componentName: ComponentName,
    private val lifecycleOwner: LifecycleOwner,
    private val onConnectionChanged: OnConnectionChanged,
    private val permissionManager: PermissionManager,
) : IMediaControllerCallback,
    IMediaConnectionCallback {

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            componentName,
            MusicServiceConnection(this),
            null
        )
    }

    private var job: Job? = null

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = ConflatedBroadcastChannel<MusicServiceConnectionState>()

    private val metadataPublisher = MutableLiveData<PlayerMetadata>()
    private val statePublisher = MutableLiveData<PlayerPlaybackState>()
    private val repeatModePublisher = MutableLiveData<PlayerRepeatMode>()
    private val shuffleModePublisher = MutableLiveData<PlayerShuffleMode>()
    private val queuePublisher = ConflatedBroadcastChannel<List<PlayerItem>>(listOf())

    fun connect() {
        if (!permissionManager.hasPermissions(Permission.Storage)) {
            Log.w("MediaExposer", "Storage permission is not granted")
            return
        }
        job?.cancel()
        job = lifecycleOwner.lifecycleScope.launch {
            for (state in connectionPublisher.openSubscription()) {
                Log.d("MediaExposer", "Connection state=$state")
                when (state) {
                    MusicServiceConnectionState.CONNECTED -> {
                        onConnectionChanged.onConnectedSuccess(mediaBrowser, callback)
                    }
                    MusicServiceConnectionState.FAILED -> onConnectionChanged.onConnectedFailed(
                        mediaBrowser,
                        callback
                    )
                }
            }
        }

        try {
            mediaBrowser.connect()
        } catch (ex: Exception) {
            ex.printStackTrace()
//            TODO leak ??
//            connect() called while neither disconnecting nor disconnected (state=CONNECT_STATE_CONNECTING)
//            connect() called while not disconnected (state=CONNECT_STATE_CONNECTING)
        }
    }

    fun disconnect() {
        job?.cancel()
        try {
            mediaBrowser.disconnect()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * Populate publishers with current data
     */
    fun initialize(mediaController: MediaControllerCompat) {
        callback.onMetadataChanged(mediaController.metadata)
        callback.onPlaybackStateChanged(mediaController.playbackState)
        callback.onRepeatModeChanged(mediaController.repeatMode)
        callback.onShuffleModeChanged(mediaController.shuffleMode)
        callback.onQueueChanged(mediaController.queue)
    }

    override fun onConnectionStateChanged(state: MusicServiceConnectionState) {
        connectionPublisher.trySend(state)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let {
            metadataPublisher.value = PlayerMetadata(it)
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.let {
            statePublisher.value = PlayerPlaybackState(it)
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        repeatModePublisher.value = PlayerRepeatMode.of(repeatMode)
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        shuffleModePublisher.value = PlayerShuffleMode.of(shuffleMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        if (queue == null) {
            return
        }
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            val result = queue.map { it.toDisplayableItem() }
            queuePublisher.trySend(result)
        }
    }

    fun observeMetadata(): LiveData<PlayerMetadata> = metadataPublisher
        .distinctUntilChanged()

    fun observePlaybackState(): LiveData<PlayerPlaybackState> = statePublisher
        .distinctUntilChanged()

    fun observeRepeat(): LiveData<PlayerRepeatMode> = repeatModePublisher
        .distinctUntilChanged()

    fun observeShuffle(): LiveData<PlayerShuffleMode> = shuffleModePublisher
        .distinctUntilChanged()

    fun observeQueue(): Flow<List<PlayerItem>> = queuePublisher
        .asFlow()
        .distinctUntilChanged()


    private fun MediaSessionCompat.QueueItem.toDisplayableItem(): PlayerItem {
        val description = this.description

        return PlayerItem(
            MediaId.fromString(description.mediaId!!),
            description.title!!.toString(),
            description.subtitle!!.toString(),
            this.queueId
        )
    }
}