package dev.olog.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.core.MediaId
import dev.olog.core.schedulers.Schedulers
import dev.olog.intents.Classes
import dev.olog.media.connection.IMediaConnectionCallback
import dev.olog.media.connection.MusicServiceConnection
import dev.olog.media.connection.MusicServiceConnectionState
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.controller.IMediaControllerCallback
import dev.olog.media.controller.MediaControllerCallback
import dev.olog.media.model.*
import dev.olog.shared.android.Permissions
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber

class MediaExposer(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChanged,
    private val schedulers: Schedulers
) : CoroutineScope by MainScope(),
    IMediaControllerCallback,
    IMediaConnectionCallback {

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            ComponentName(context, Classes.SERVICE_MUSIC),
            MusicServiceConnection(this),
            null
        )
    }

    private var job by autoDisposeJob()

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = ConflatedBroadcastChannel<MusicServiceConnectionState>()

    private val metadataPublisher = ConflatedBroadcastChannel<PlayerMetadata>()
    private val statePublisher = ConflatedBroadcastChannel<PlayerPlaybackState>()
    private val repeatModePublisher = ConflatedBroadcastChannel<PlayerRepeatMode>()
    private val shuffleModePublisher = ConflatedBroadcastChannel<PlayerShuffleMode>()
    private val queuePublisher = ConflatedBroadcastChannel<List<PlayerItem>>(listOf())

    fun connect() {
        if (!Permissions.canReadStorage(context)) {
            Timber.w("MediaExposer: Storage permission is not granted")
            return
        }
        job = launch {
            // TODO refactor to flow
            for (state in connectionPublisher.openSubscription()) {
                Timber.d("MediaExposer: Connection state=$state")
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
                Timber.e(ex)
//                TODO leak ??
//                connect() called while neither disconnecting nor disconnected (state=CONNECT_STATE_CONNECTING)
//                connect() called while not disconnected (state=CONNECT_STATE_CONNECTING)
            }
        }
    }

    fun disconnect() {
        job = null
        if (mediaBrowser.isConnected){
            mediaBrowser.disconnect()
        }
    }

    fun dispose() {
        connectionPublisher.close()

        metadataPublisher.close()
        statePublisher.close()
        repeatModePublisher.close()
        shuffleModePublisher.close()
        queuePublisher.close()
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
        connectionPublisher.offer(state)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat) {
        metadataPublisher.offer(PlayerMetadata(metadata))
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        statePublisher.offer(PlayerPlaybackState(state))
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        repeatModePublisher.offer(PlayerRepeatMode.of(repeatMode))
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        shuffleModePublisher.offer(PlayerShuffleMode.of(shuffleMode))
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>) {
        // TODO keep in a job
        launch(schedulers.cpu) {
            val result = queue.map { it.toDisplayableItem() }
            queuePublisher.offer(result)
        }
    }

    fun observeMetadata(): Flow<PlayerMetadata> = metadataPublisher.asFlow()
        .distinctUntilChanged()

    fun observePlaybackState(): Flow<PlayerPlaybackState> = statePublisher.asFlow()
        .distinctUntilChanged()

    fun observeRepeat(): Flow<PlayerRepeatMode> = repeatModePublisher.asFlow()
        .distinctUntilChanged()

    fun observeShuffle(): Flow<PlayerShuffleMode> = shuffleModePublisher.asFlow()
        .distinctUntilChanged()

    fun observeQueue(): Flow<List<PlayerItem>> = queuePublisher.asFlow()
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