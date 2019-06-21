package dev.olog.msc.music.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.constants.WidgetConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.playing.queue.UpdateMiniQueueUseCase
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.presentation.app.widget.WidgetClasses
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.getAppWidgetsIdsFor
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MediaSessionQueue @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSession: MediaSessionCompat,
        private val playerState: PlayerState,
        private val updateMiniQueueUseCase: UpdateMiniQueueUseCase,
        private val widgetClasses: WidgetClasses

) : DefaultLifecycleObserver {

    private val publisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private val immediatePublisher : PublishSubject<MediaSessionQueueModel<MediaEntity>> = PublishSubject.create()
    private var miniQueueDisposable : Disposable? = null
    private var immediateMiniQueueDisposable : Disposable? = null
    private var updateMiniQueueDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)

        miniQueueDisposable = publisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .debounce(1, TimeUnit.SECONDS)
                .doOnNext { persistMiniQueue(it.queue) }
                .map { it.toQueueItem() }
                .subscribe({ (id, queue) ->
                    mediaSession.setQueue(queue)
                    playerState.updateActiveQueueId(id)
                }, Throwable::printStackTrace)

        immediateMiniQueueDisposable = immediatePublisher
                .toSerialized()
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .doOnNext { persistMiniQueue(it.queue) }
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

    private fun persistMiniQueue(tracks: List<MediaEntity>){
        updateMiniQueueDisposable.unsubscribe()
        updateMiniQueueDisposable = updateMiniQueueUseCase.execute(tracks)
                .subscribe({ notifyWidgets() }, Throwable::printStackTrace)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        miniQueueDisposable.unsubscribe()
        immediateMiniQueueDisposable.unsubscribe()
        updateMiniQueueDisposable.unsubscribe()
    }

    private fun notifyWidgets(){
        for (clazz in widgetClasses.get()) {
            val ids = context.getAppWidgetsIdsFor(clazz)
            val intent = Intent(context, clazz).apply {
                action = WidgetConstants.QUEUE_CHANGED
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            }
            context.sendBroadcast(intent)
        }
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