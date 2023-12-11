package dev.olog.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.olog.core.MediaId
import dev.olog.intents.Classes
import dev.olog.media.connection.IMediaConnectionCallback
import dev.olog.media.connection.MusicServiceConnection
import dev.olog.media.connection.MusicServiceConnectionState
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.controller.IMediaControllerCallback
import dev.olog.media.controller.MediaControllerCallback
import dev.olog.media.model.PlayerItem
import dev.olog.media.model.PlayerMetadata
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.media.model.PlayerRepeatMode
import dev.olog.media.model.PlayerShuffleMode
import dev.olog.shared.android.Permissions
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaExposer(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChanged,
    private val scope: CoroutineScope,
) : IMediaControllerCallback,
    IMediaConnectionCallback {

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            ComponentName(context, Classes.SERVICE_MUSIC),
            MusicServiceConnection(this),
            null
        )
    }

    private var job: Job? = null

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = MutableSharedFlow<MusicServiceConnectionState>()

    private val metadataPublisher = MutableLiveData<PlayerMetadata>()
    private val statePublisher = MutableLiveData<PlayerPlaybackState>()
    private val repeatModePublisher = MutableLiveData<PlayerRepeatMode>()
    private val shuffleModePublisher = MutableLiveData<PlayerShuffleMode>()
    private val queuePublisher = MutableStateFlow<List<PlayerItem>>(listOf())

    fun connect() {
        if (!Permissions.canReadStorage(context)) {
            Log.w("MediaExposer", "Storage permission is not granted")
            return
        }
        job?.cancel()
        job = scope.launch {
            connectionPublisher.collect { state ->
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

    fun disconnect() {
        job?.cancel()
        if (mediaBrowser.isConnected){
            mediaBrowser.disconnect()
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
        connectionPublisher.tryEmit(state)
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

    fun observeMetadata(): LiveData<PlayerMetadata> = metadataPublisher
        .distinctUntilChanged()

    fun observePlaybackState(): LiveData<PlayerPlaybackState> = statePublisher
        .distinctUntilChanged()

    fun observeRepeat(): LiveData<PlayerRepeatMode> = repeatModePublisher
        .distinctUntilChanged()

    fun observeShuffle(): LiveData<PlayerShuffleMode> = shuffleModePublisher
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