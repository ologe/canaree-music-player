package dev.olog.msc.presentation.player

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.constants.Theme
import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.msc.presentation.utils.images.ImageProcessorResult
import dev.olog.msc.presentation.widget.image.view.toPlayerImage
import dev.olog.msc.pro.IBilling
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getBitmapAsync
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val billing: IBilling,
        private val appPrefsUseCase: AppPreferencesUseCase,
        musicPrefsUseCase: MusicPreferencesUseCase

) : ViewModel() {

    private var disposable: Disposable? = null
    private val colorsPublisher = BehaviorProcessor.create<ImageProcessorResult>()

    private var fullScreenVisible = true

    private val miniQueue = MutableLiveData<List<DisplayableItem>>()

    var currentTrackId : Long = -1

    fun observeMiniQueue() : LiveData<List<DisplayableItem>> = miniQueue

    fun updateQueue(list: List<DisplayableItem>){
        miniQueue.postValue(list)
    }

    fun onMetadataChanged(context: Context, metadata: MediaMetadataCompat){
        disposable.unsubscribe()
        disposable = Single.fromCallable { true }
                .filter { AppConstants.THEME.isFlat() }
                .map { metadata.toPlayerImage() }
                .map { context.getBitmapAsync(it, 200) }
                .subscribeOn(Schedulers.io())
                .map { ImageProcessor(context).processImage(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(colorsPublisher::onNext, Throwable::printStackTrace)
    }

    fun updateFullscreen(): Boolean {
        fullScreenVisible = !fullScreenVisible
        return fullScreenVisible
    }

    fun observeImageColors(): Flowable<ImageProcessorResult> = colorsPublisher

    fun observePlayerControlsVisibility(): Observable<Boolean> {
        return Observables.combineLatest(
                billing.observeIsPremium(),
                appPrefsUseCase.observePlayerControlsVisibility(), { premium, show -> premium && show }
        )
    }

    private val progressPublisher = BehaviorSubject.createDefault(0)

    val observeProgress : Observable<Int> = progressPublisher

    fun updateProgress(progress: Int){
        progressPublisher.onNext(progress)
    }

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    fun playerControls(): DisplayableItem {
        val id = when (AppConstants.THEME) {
            Theme.DEFAULT -> R.layout.fragment_player_controls
            Theme.FLAT -> R.layout.fragment_player_controls_flat
            Theme.SPOTIFY -> R.layout.fragment_player_controls_spotify
            Theme.FULLSCREEN -> R.layout.fragment_player_controls_fullscreen
        }
        return DisplayableItem(id, MediaId.headerId("player controls id"), "")
    }

    val onFavoriteStateChanged: Observable<FavoriteEnum> = observeFavoriteAnimationUseCase.execute()

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()

    override fun onCleared() {
        disposable.unsubscribe()
    }

}