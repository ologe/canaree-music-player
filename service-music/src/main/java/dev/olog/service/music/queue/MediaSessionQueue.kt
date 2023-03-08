package dev.olog.service.music.queue

import android.app.Service
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import dev.olog.core.schedulers.Schedulers
import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.android.extensions.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

class MediaSessionQueue @Inject constructor(
    service: Service,
    schedulers: Schedulers,
    private val mediaSession: MediaSessionCompat
) {

    companion object {
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val delayedChannel = ConflatedBroadcastChannel<List<MediaEntity>>()
    private val immediateChannel = ConflatedBroadcastChannel<List<MediaEntity>>()

    init {
        service.lifecycleScope.launch(schedulers.cpu) {
            delayedChannel.asFlow()
                .debounce(DELAY)
                .collect { publish(it) }
        }

        service.lifecycleScope.launch(schedulers.cpu) {
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

