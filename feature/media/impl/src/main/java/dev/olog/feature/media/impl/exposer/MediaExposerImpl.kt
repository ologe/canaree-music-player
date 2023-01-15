package dev.olog.feature.media.impl.exposer

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MediaExposer
import dev.olog.feature.media.api.connection.MusicServiceConnectionState
import dev.olog.feature.media.api.connection.OnConnectionChanged
import dev.olog.feature.media.api.model.*
import dev.olog.feature.media.impl.MusicService
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import dev.olog.shared.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class MediaExposerImpl(
    private val context: Context,
    private val lifecycleScope: CoroutineScope,
    private val onConnectionChanged: OnConnectionChanged,
    private val permissionManager: PermissionManager,
) : MediaExposer {

    class Factory : MediaExposer.Factory {
        override fun create(
            context: Context,
            lifecycle: Lifecycle,
            onConnectionChanged: OnConnectionChanged,
            permissionManager: PermissionManager,
        ): MediaExposer = MediaExposerImpl(
            context = context,
            lifecycleScope = lifecycle.coroutineScope,
            onConnectionChanged = onConnectionChanged,
            permissionManager = permissionManager,
        )
    }

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            ComponentName(context, MusicService::class.java),
            MusicServiceConnection(this),
            null
        )
    }

    private var job: Job? = null

    override val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = ConflatedBroadcastChannel<MusicServiceConnectionState>()

    private val metadataPublisher = MutableLiveData<PlayerMetadata>()
    private val statePublisher = MutableLiveData<PlayerPlaybackState>()
    private val repeatModePublisher = MutableLiveData<PlayerRepeatMode>()
    private val shuffleModePublisher = MutableLiveData<PlayerShuffleMode>()
    private val queuePublisher = ConflatedBroadcastChannel<List<PlayerItem>>(listOf())

    override fun connect() {
        if (!permissionManager.hasMandatoryPermissions()) {
            Log.w("MediaExposer", "Storage permission is not granted")
            return
        }
        job?.cancel()
        job = lifecycleScope.launch {
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

        if (!mediaBrowser.isConnected){
            try {
                mediaBrowser.connect()
            } catch (ex: IllegalStateException){
//                TODO leak ??
//                connect() called while neither disconnecting nor disconnected (state=CONNECT_STATE_CONNECTING)
//                connect() called while not disconnected (state=CONNECT_STATE_CONNECTING)
            }
        }
    }

    override fun disconnect() {
        job?.cancel()
        if (mediaBrowser.isConnected){
            mediaBrowser.disconnect()
        }
    }

    /**
     * Populate publishers with current data
     */
    override fun initialize(mediaController: MediaControllerCompat) {
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
        lifecycleScope.launch(Dispatchers.Default) {
            val result = queue.map { it.toDisplayableItem() }
            queuePublisher.trySend(result)
        }
    }

    override fun observeMetadata(): LiveData<PlayerMetadata> = metadataPublisher
        .distinctUntilChanged()

    override fun observePlaybackState(): LiveData<PlayerPlaybackState> = statePublisher
        .distinctUntilChanged()

    override fun observeRepeat(): LiveData<PlayerRepeatMode> = repeatModePublisher
        .distinctUntilChanged()

    override fun observeShuffle(): LiveData<PlayerShuffleMode> = shuffleModePublisher
        .distinctUntilChanged()

    override fun observeQueue(): Flow<List<PlayerItem>> = queuePublisher
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