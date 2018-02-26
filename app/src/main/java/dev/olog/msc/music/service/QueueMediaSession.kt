package dev.olog.msc.music.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.mapToList
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QueueMediaSession @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSession: MediaSessionCompat

) : DefaultLifecycleObserver {

    private val publisher : PublishSubject<List<MediaEntity>> = PublishSubject.create()
    private val immediatePublisher : PublishSubject<List<MediaEntity>> = PublishSubject.create()
    private var miniQueueDisposable : Disposable? = null
    private var immediateMiniQueueDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        miniQueueDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .debounce(2, TimeUnit.SECONDS)
                .mapToList { it.toQueueItem() }
                .subscribe(mediaSession::setQueue, Throwable::printStackTrace)

        immediateMiniQueueDisposable = immediatePublisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .mapToList { it.toQueueItem() }
                .subscribe(mediaSession::setQueue, Throwable::printStackTrace)
    }

    fun onNext(list: List<MediaEntity>){
        publisher.onNext(list)
    }

    fun onNextImmediate(list: List<MediaEntity>){
        immediatePublisher.onNext(list)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        miniQueueDisposable.unsubscribe()
        immediateMiniQueueDisposable.unsubscribe()
    }

    private fun MediaEntity.toQueueItem() : MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.songId(this.idInPlaylist.toLong()).toString())
                .setTitle(this.title)
                .setSubtitle(this.artist)
                .setMediaUri(Uri.parse(this.image))
                .build()

        return MediaSessionCompat.QueueItem(description, this.idInPlaylist.toLong())
    }

}