package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import dev.olog.domain.interactor.music_service.UpdateMiniQueueUseCase
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
import dev.olog.shared.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.toFlowable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QueueMediaSession @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSession: MediaSessionCompat,
        updateMiniQueueUseCase: UpdateMiniQueueUseCase

) : DefaultLifecycleObserver {

    private val publisher : BehaviorProcessor<List<MediaEntity>> = BehaviorProcessor.create()
    private var notificationQueueDisposable : Disposable? = null
    private var miniQueueDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        miniQueueDisposable = publisher.debounce(50, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .groupMap { it.id }
                .subscribe(updateMiniQueueUseCase::execute, Throwable::printStackTrace)

        notificationQueueDisposable = publisher.debounce(50, TimeUnit.MILLISECONDS)
                .delay(100, TimeUnit.MILLISECONDS)
                .flatMapSingle { it.toFlowable()
                        .take(6)
                        .map { it.toQueueItem() }
                        .toList()
                }.subscribe(mediaSession::setQueue, Throwable::printStackTrace)
    }

    fun onNext(list: List<MediaEntity>){
        publisher.onNext(list)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        notificationQueueDisposable.unsubscribe()
        miniQueueDisposable.unsubscribe()
    }

    private fun MediaEntity.toQueueItem() : MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
                .setMediaId(MediaId.songId(this.id).toString())
                .setTitle(this.title)
                .setSubtitle(this.artist)
                .build()

        return MediaSessionCompat.QueueItem(description, this.id)
    }

}