package dev.olog.feature.media.api

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
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
import dev.olog.platform.permission.Permission
import dev.olog.platform.permission.PermissionManager
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.extension.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaExposer(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChanged,
    private val scope: CoroutineScope,
    private val componentName: ComponentName,
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

    private var job by autoDisposeJob()

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = Channel<MusicServiceConnectionState>()

    private val metadataPublisher = MutableStateFlow<PlayerMetadata?>(null)
    private val statePublisher = MutableStateFlow<PlayerPlaybackState?>(null)
    private val repeatModePublisher = MutableStateFlow<PlayerRepeatMode?>(null)
    private val shuffleModePublisher = MutableStateFlow<PlayerShuffleMode?>(null)
    private val queuePublisher = MutableStateFlow<List<PlayerItem>>(emptyList())

    suspend fun connect() = coroutineScope {
        permissionManager.awaitPermission(context, Permission.Storage)

        job = connectionPublisher.receiveAsFlow()
            .onEach { handleConnectionChange(it)}
            .launchIn(scope)

        tryConnect()
    }

    fun disconnect() {
        job = null
        mediaBrowser.disconnect()
    }

    private fun handleConnectionChange(state: MusicServiceConnectionState) = when (state) {
        MusicServiceConnectionState.CONNECTED -> {
            onConnectionChanged.onConnectedSuccess(mediaBrowser, callback)
        }
        MusicServiceConnectionState.FAILED -> {
            onConnectionChanged.onConnectedFailed(mediaBrowser, callback)
        }
    }

    private suspend fun tryConnect() = coroutineScope {
        while (isActive) {
            try {
                mediaBrowser.connect()
                return@coroutineScope
            } catch (ignored: IllegalStateException) {
                // thrown while still disconnecting, can ignore
            }
            delay(100)
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
        scope.launch(Dispatchers.Default) {
            val result = queue.map { it.toDisplayableItem() }
            queuePublisher.value = result
        }
    }

    fun observeMetadata(): Flow<PlayerMetadata> = metadataPublisher.filterNotNull()

    fun observePlaybackState(): Flow<PlayerPlaybackState> = statePublisher.filterNotNull()

    fun observeRepeat(): Flow<PlayerRepeatMode> = repeatModePublisher
        .filterNotNull()
        .distinctUntilChanged()

    fun observeShuffle(): Flow<PlayerShuffleMode> = shuffleModePublisher
        .filterNotNull()
        .distinctUntilChanged()

    fun observeQueue(): Flow<List<PlayerItem>> = queuePublisher


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