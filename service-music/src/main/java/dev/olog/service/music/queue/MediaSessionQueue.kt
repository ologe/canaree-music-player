package dev.olog.service.music.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.core.ServiceLifecycle
import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.CustomScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val mediaSession: MediaSessionCompat
) : DefaultLifecycleObserver,
    CoroutineScope by CustomScope() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionQueue::class.java.simpleName}"
        private const val DELAY = 1000L
    }

    private val delayedChannel = MutableSharedFlow<List<MediaEntity>>()
    private val immediateChannel = MutableSharedFlow<List<MediaEntity>>()

    init {
        lifecycle.addObserver(this)

        launch {
            delayedChannel
                .debounce(DELAY)
                .collect { publish(it) }
        }

        launch {
            immediateChannel
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
        delayedChannel.tryEmit(list)
    }

    fun onNextImmediate(list: List<MediaEntity>) {
        Log.v(TAG, "on next immediate")
        immediateChannel.tryEmit(list)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cancel()
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

