package dev.olog.feature.media.impl.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import dev.olog.feature.media.impl.model.MediaEntity
import dev.olog.injection.dagger.ServiceLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val mediaSession: MediaSessionCompat
) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val delayedChannel = ConflatedBroadcastChannel<List<MediaEntity>>()
    private val immediateChannel = ConflatedBroadcastChannel<List<MediaEntity>>()

    init {
        lifecycle.coroutineScope.launch(Dispatchers.Default) {
            delayedChannel.asFlow()
                .debounce(DELAY)
                .collect { publish(it) }
        }

        lifecycle.coroutineScope.launch(Dispatchers.Default) {
            immediateChannel.asFlow()
                .collect { publish(it) }
        }
    }

    private suspend fun publish(list: List<MediaEntity>) {
        Log.v(TAG, "publish")
        val queue = list.map { it.toQueueItem() }

        withContext(Dispatchers.Main) {
            mediaSession.setQueue(queue)
        }
    }

    fun onNext(list: List<MediaEntity>) {
        Log.v(TAG, "on next delayed")
        delayedChannel.trySend(list)
    }

    fun onNextImmediate(list: List<MediaEntity>) {
        Log.v(TAG, "on next immediate")
        immediateChannel.trySend(list)
    }

    private fun MediaEntity.toQueueItem(): MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(mediaId.toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .build()

        return MediaSessionCompat.QueueItem(description, this.idInPlaylist.toLong())
    }

}

