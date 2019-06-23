package dev.olog.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.olog.media.connection.IMediaConnectionCallback
import dev.olog.media.connection.MusicServiceConnection
import dev.olog.media.connection.MusicServiceConnectionState
import dev.olog.media.connection.OnConnectionChanged
import dev.olog.media.controller.IMediaControllerCallback
import dev.olog.media.controller.MediaControllerCallback
import dev.olog.shared.Permissions
import dev.olog.shared.extensions.distinctUntilChanged
import dev.olog.shared.extensions.filter
import dev.olog.shared.lazyFast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MediaExposer(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChanged
) : CoroutineScope by MainScope(),
    IMediaControllerCallback,
    IMediaConnectionCallback {

    companion object {
        const val MUSIC_SERVICE = "dev.olog.msc.music.service.MusicService"
    }

    private val mediaBrowser: MediaBrowserCompat by lazyFast {
        MediaBrowserCompat(
            context,
            ComponentName(context, MUSIC_SERVICE),
            MusicServiceConnection(this), null
        )
    }

    private var job: Job? = null

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)

    private val connectionPublisher = BroadcastChannel<MusicServiceConnectionState>(Channel.CONFLATED)

    private val metadataPublisher = MutableLiveData<MediaMetadataCompat>()
    private val statePublisher = MutableLiveData<PlaybackStateCompat>()
    private val repeatModePublisher = MutableLiveData<Int>()
    private val shuffleModePublisher = MutableLiveData<Int>()
    private val queuePublisher = MutableLiveData<List<MediaSessionCompat.QueueItem>>()
    private val queueTitlePublisher = MutableLiveData<String>()
    private val extrasPublisher = MutableLiveData<Bundle>()

    fun connect() {
        if (!Permissions.canReadStorage(context)) {
            Log.w("MediaExposer", "Read storage permission is not granted")
            return
        }
        job?.cancel()
        job = launch {
            for (state in connectionPublisher.openSubscription()) {
                Log.d("MediaExposer", "Connection state=$state")
                when (state) {
                    MusicServiceConnectionState.CONNECTED -> {
                        onConnectionChanged.onConnectedSuccess(mediaBrowser, callback)
                    }
                    MusicServiceConnectionState.FAILED -> onConnectionChanged.onConnectedFailed(mediaBrowser, callback)
                }
            }
        }

        mediaBrowser.connect()
    }

    fun disconnect() {
        job?.cancel()
        mediaBrowser.disconnect()
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
        launch { connectionPublisher.send(state) }
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadataPublisher.value = metadata
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        statePublisher.value = state
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        repeatModePublisher.value = repeatMode
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        shuffleModePublisher.value = shuffleMode
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        queuePublisher.value = queue
    }

    override fun onQueueTitleChanged(title: CharSequence?) {
        queueTitlePublisher.value = title.toString()
    }

    override fun onExtrasChanged(extras: Bundle?) {
        extrasPublisher.value = extras
    }

    fun observeMetadata(): LiveData<MediaMetadataCompat> = metadataPublisher
        .filter { it != null }
        .distinctUntilChanged()

    fun observePlaybackState(): LiveData<PlaybackStateCompat> = statePublisher
        .filter { it != null }
        .distinctUntilChanged()

    fun observeRepeat(): LiveData<Int> = repeatModePublisher
        .filter { it != null }
        .distinctUntilChanged()

    fun observeShuffle(): LiveData<Int> = shuffleModePublisher
        .filter { it != null }
        .distinctUntilChanged()

    fun observeQueueTitle(): LiveData<String> = queueTitlePublisher
        .filter { it != null }
        .distinctUntilChanged()

    fun observeExtras(): LiveData<Bundle> = extrasPublisher.filter { it != null }

    fun observeQueue(): LiveData<List<MediaSessionCompat.QueueItem>> = queuePublisher
        .filter { it != null }
        .distinctUntilChanged()

}