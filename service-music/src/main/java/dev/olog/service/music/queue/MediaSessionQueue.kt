package dev.olog.service.music.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.coroutines.DispatcherScope
import dev.olog.domain.schedulers.Schedulers
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.model.MediaEntity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val mediaSession: MediaSessionCompat,
    private val schedulers: Schedulers
) : DefaultLifecycleObserver {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val scope by DispatcherScope(schedulers.cpu)
    private val delayedChannel = ConflatedBroadcastChannel<List<MediaEntity>>()
    private val immediateChannel = ConflatedBroadcastChannel<List<MediaEntity>>()

    init {
        lifecycle.addObserver(this)

        delayedChannel.asFlow()
            .debounce(DELAY)
            .onEach { publish(it) }
            .launchIn(scope)

        immediateChannel.asFlow()
            .onEach { publish(it) }
            .launchIn(scope)
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

    override fun onDestroy(owner: LifecycleOwner) {
        delayedChannel.close()
        immediateChannel.close()
        scope.cancel()
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

