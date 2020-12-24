package dev.olog.service.music.queue

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.olog.service.music.model.MediaEntity
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MediaSessionQueue @Inject constructor(
    private val lifecycleOwner: LifecycleOwner,
    private val mediaSession: MediaSessionCompat
) {

    private var publishJob by autoDisposeJob()

    private suspend fun publish(list: List<MediaEntity>) {
        val queue = list.map { it.toQueueItem() }

        withContext(Dispatchers.Main) {
            mediaSession.setQueue(queue)
        }
    }

    fun onNext(list: List<MediaEntity>) {
        publishJob = lifecycleOwner.lifecycleScope.launch {
            publish(list)
        }
    }

    private fun MediaEntity.toQueueItem(): MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(mediaId.toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .build()

        return MediaSessionCompat.QueueItem(description, this.progressive.toLong())
    }

}

