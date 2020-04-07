package dev.olog.lib.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.shared.coroutines.MainScope
import dev.olog.shared.coroutines.autoDisposeJob
import dev.olog.domain.MediaId
import dev.olog.domain.schedulers.Schedulers
import dev.olog.intents.Classes
import dev.olog.lib.media.connection.IMediaConnectionCallback
import dev.olog.lib.media.connection.MusicServiceConnection
import dev.olog.lib.media.connection.MusicServiceConnectionState
import dev.olog.lib.media.connection.OnConnectionChanged
import dev.olog.lib.media.controller.IMediaControllerCallback
import dev.olog.lib.media.controller.MediaControllerCallback
import dev.olog.lib.media.model.*
import dev.olog.shared.lazyFast
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MediaExposer(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChanged,
    private val schedulers: Schedulers,
    private val config: Config = Config(),
    private val canReadStorage: () -> Boolean
) : IMediaControllerCallback,
    IMediaConnectionCallback {

    class Config(
        val connectionPublisher: ConflatedBroadcastChannel<MusicServiceConnectionState> = ConflatedBroadcastChannel(),
        val metadataPublisher: ConflatedBroadcastChannel<PlayerMetadata> = ConflatedBroadcastChannel(),
        val statePublisher: ConflatedBroadcastChannel<PlayerPlaybackState> = ConflatedBroadcastChannel(),
        val repeatModePublisher: ConflatedBroadcastChannel<PlayerRepeatMode> = ConflatedBroadcastChannel(),
        val shuffleModePublisher: ConflatedBroadcastChannel<PlayerShuffleMode> = ConflatedBroadcastChannel(),
        val queuePublisher: ConflatedBroadcastChannel<List<PlayerItem>> = ConflatedBroadcastChannel(emptyList())
    ) {

        fun dispose() {
            connectionPublisher.close()
            metadataPublisher.close()
            statePublisher.close()
            repeatModePublisher.close()
            shuffleModePublisher.close()
            queuePublisher.close()
        }

    }

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            ComponentName(context, Classes.SERVICE_MUSIC),
            MusicServiceConnection(this),
            null
        )
    }

    private val scope by MainScope()

    private var connectionJob by autoDisposeJob()
    private var queueJob by autoDisposeJob()

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    fun connect() {
        if (!canReadStorage()) {
            Timber.w("MediaExposer: Storage permission is not granted")
            return
        }
        connectionJob = config.connectionPublisher.asFlow()
            .onEach { state ->
                when (state) {
                    MusicServiceConnectionState.CONNECTED -> {
                        onConnectionChanged.onConnectedSuccess(mediaBrowser, callback)
                    }
                    MusicServiceConnectionState.FAILED -> {
                        onConnectionChanged.onConnectedFailed(mediaBrowser, callback)
                    }
                }
            }.launchIn(scope)

        if (!mediaBrowser.isConnected) {
            try {
                mediaBrowser.connect()
            } catch (ex: IllegalStateException){
                Timber.e(ex)
            }
        }
    }

    fun disconnect() {
        connectionJob = null
        queueJob = null
        if (mediaBrowser.isConnected){
            mediaBrowser.disconnect()
        }
    }

    fun dispose() {
        config.dispose()
        scope.cancel()
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
        config.connectionPublisher.offer(state)
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat) {
        config.metadataPublisher.offer(PlayerMetadata(metadata))
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        config.statePublisher.offer(PlayerPlaybackState(state))
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        config.repeatModePublisher.offer(PlayerRepeatMode.of(repeatMode))
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        config.shuffleModePublisher.offer(PlayerShuffleMode.of(shuffleMode))
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>) {
        queueJob = scope.launch(schedulers.cpu) {
            val result = queue.map { it.toDisplayableItem() }
            config.queuePublisher.offer(result)
        }
    }

    fun observeMetadata(): Flow<PlayerMetadata> = config.metadataPublisher.asFlow()
        .distinctUntilChanged()

    fun observePlaybackState(): Flow<PlayerPlaybackState> = config.statePublisher.asFlow()
        .distinctUntilChanged()

    fun observeRepeat(): Flow<PlayerRepeatMode> = config.repeatModePublisher.asFlow()
        .distinctUntilChanged()

    fun observeShuffle(): Flow<PlayerShuffleMode> = config.shuffleModePublisher.asFlow()
        .distinctUntilChanged()

    fun observeQueue(): Flow<List<PlayerItem>> = config.queuePublisher.asFlow()
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