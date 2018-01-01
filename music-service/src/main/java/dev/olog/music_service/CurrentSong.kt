package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import dev.olog.domain.interactor.detail.most_played.InsertMostPlayedUseCase
import dev.olog.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.domain.interactor.floating_info.SetFloatingInfoRequestUseCase
import dev.olog.domain.interactor.music_service.InsertHistorySongUseCase
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.model.MediaEntity
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaId
import dev.olog.shared.unsubscribe
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@PerService
class CurrentSong @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        insertMostPlayedUseCase: InsertMostPlayedUseCase,
        insertHistorySongUseCase: InsertHistorySongUseCase,
        private val isFavoriteSongUseCase: IsFavoriteSongUseCase,
        private val setFloatingInfoRequestUseCase: SetFloatingInfoRequestUseCase

) : DefaultLifecycleObserver {

    private val publisher = BehaviorProcessor.create<MediaEntity>()

    private var mostPlayedDisposable : Disposable? = null
    private var historyDisposable : Disposable? = null
    private var isFavoriteDisposable : Disposable? = null

    private val insertToMostPlayedFlowable = publisher
            .observeOn(Schedulers.io())
            .flatMapMaybe { createMostPlayedId(it) }
            .flatMapCompletable { insertMostPlayedUseCase.execute(it) }

    private val insertHistorySongFlowable = publisher
            .observeOn(Schedulers.io())
            .flatMapCompletable { insertHistorySongUseCase.execute(it.id) }

    init {
        lifecycle.addObserver(this)

        mostPlayedDisposable = insertToMostPlayedFlowable.subscribe()
        historyDisposable = insertHistorySongFlowable.subscribe()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mostPlayedDisposable.unsubscribe()
        historyDisposable.unsubscribe()
        isFavoriteDisposable.unsubscribe()
    }

    fun update(mediaEntity: MediaEntity){
        publisher.onNext(mediaEntity)

        isFavoriteDisposable.unsubscribe()
        isFavoriteDisposable = isFavoriteSongUseCase
                .execute(mediaEntity.id)
                .subscribe()
    }

    fun setFloatingInfoCurrentItem(mediaEntity: MediaEntity){
        var result = mediaEntity.title
        if (mediaEntity.artist != context.getString(R.string.unknown_artist)){
            result += " ${mediaEntity.artist}"
        }
        setFloatingInfoRequestUseCase.execute(mediaEntity.title)
    }

    private fun createMostPlayedId(entity: MediaEntity): Maybe<MediaId> {
        try {
            return Maybe.just(MediaId.playableItem(entity.mediaId, entity.id))
        } catch (ex: Exception){
            return Maybe.empty()
        }
    }

}