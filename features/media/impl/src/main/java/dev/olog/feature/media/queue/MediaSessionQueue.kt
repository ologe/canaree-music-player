package dev.olog.feature.media.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import dev.olog.core.ServiceScope
import dev.olog.feature.media.api.model.MediaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val serviceScope: ServiceScope,
) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val delayedChannel = ConflatedBroadcastChannel<List<MediaEntity>>()
    private val immediateChannel = ConflatedBroadcastChannel<List<MediaEntity>>()

    init {
        delayedChannel.asFlow()
            .debounce(DELAY)
            .onEach { publish(it) }
            .launchIn(serviceScope)

        immediateChannel.asFlow()
            .onEach { publish(it) }
            .launchIn(serviceScope)
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

