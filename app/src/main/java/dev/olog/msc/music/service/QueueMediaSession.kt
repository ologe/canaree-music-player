package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QueueMediaSession @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSession: MediaSessionCompat,
        private val playerState: PlayerState

) : DefaultLifecycleObserver {

    private val publisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private val immediatePublisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private var miniQueueDisposable : Disposable? = null
    private var immediateMiniQueueDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)

        miniQueueDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .debounce(1, TimeUnit.SECONDS)
                .map { it.toQueueItem() }
                .subscribe({ (id, queue) ->
                    mediaSession.setQueue(queue)
                    playerState.updateActiveQueueId(id)
                }, Throwable::printStackTrace)

        immediateMiniQueueDisposable = immediatePublisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .map { it.toQueueItem() }
                .subscribe({ (id, queue) ->
                    mediaSession.setQueue(queue)
                    playerState.updateActiveQueueId(id)
                }, Throwable::printStackTrace)
    }

    fun onNext(list: MediaSessionQueueModel<MediaEntity>){
        publisher.onNext(list)
    }

    fun onNextImmediate(list: MediaSessionQueueModel<MediaEntity>){
        immediatePublisher.onNext(list)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        miniQueueDisposable.unsubscribe()
        immediateMiniQueueDisposable.unsubscribe()
    }

    private fun MediaEntity.toQueueItem() : MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.songId(this.id).toString())
                .setTitle(this.title)
                .setSubtitle(DisplayableItem.adjustArtist(this.artist))
                .setMediaUri(Uri.parse(this.image))
                .build()

        return MediaSessionCompat.QueueItem(description, this.idInPlaylist.toLong())
    }

    private fun MediaSessionQueueModel<MediaEntity>.toQueueItem(): MediaSessionQueueModel<MediaSessionCompat.QueueItem> {
        val queue = this.queue.map { it.toQueueItem() }
        return MediaSessionQueueModel(this.activeId, queue)
    }

}

data class MediaSessionQueueModel<T>(
        val activeId: Long,
        val queue: List<T>
)