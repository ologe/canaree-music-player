package dev.olog.feature.service.music.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import dev.olog.domain.schedulers.Schedulers
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.feature.service.music.model.MediaEntity
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    @ServiceLifecycle private val lifecycle: Lifecycle,
    private val mediaSession: MediaSessionCompat,
    private val schedulers: Schedulers
) : DefaultLifecycleObserver {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val delayedChannel = ConflatedBroadcastChannel<List<MediaEntity>>()
    private val immediateChannel = ConflatedBroadcastChannel<List<MediaEntity>>()

    init {
        lifecycle.addObserver(this)

        delayedChannel.asFlow()
            .debounce(DELAY)
            .onEach { publish(it) }
            .flowOn(schedulers.cpu)
            .launchIn(lifecycle.coroutineScope)

        immediateChannel.asFlow()
            .onEach { publish(it) }
            .flowOn(schedulers.cpu)
            .launchIn(lifecycle.coroutineScope)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        delayedChannel.close()
        immediateChannel.close()
    }

    private suspend fun publish(list: List<MediaEntity>) {
        Timber.v("$TAG publish")
        val queue = list.map { it.toQueueItem() }

        withContext(schedulers.main) {
            mediaSession.setQueue(queue)
        }
    }

    fun onNext(list: List<MediaEntity>) {
        Timber.v("$TAG on next delayed")
        delayedChannel.offer(list)
    }

    fun onNextImmediate(list: List<MediaEntity>) {
        Timber.v("$TAG on next immediate")
        immediateChannel.offer(list)
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

