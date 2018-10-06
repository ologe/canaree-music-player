package dev.olog.msc.presentation.player

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import dev.olog.msc.R
import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.utils.images.ImageProcessor
import dev.olog.msc.presentation.utils.images.ImageProcessorResult
import dev.olog.msc.presentation.widget.image.view.toPlayerImage
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.getBitmapAsync
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val musicPrefsUseCase: MusicPreferencesUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceUseCase

) : ViewModel() {

    private var disposable: Disposable? = null
    private var insertLyrics: Disposable? = null
    private val colorsPublisher = BehaviorProcessor.create<ImageProcessorResult>()

    private val miniQueue = MutableLiveData<List<DisplayableItem>>()

    private val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    fun getCurrentTrackId() = currentTrackIdPublisher.value!!

    fun updateCurrentTrackId(trackId: Long){
        currentTrackIdPublisher.onNext(trackId)
    }

    fun observeMiniQueue() : LiveData<List<DisplayableItem>> = miniQueue

    fun updateQueue(list: List<DisplayableItem>){
        miniQueue.postValue(list)
    }

    fun onMetadataChanged(context: Context, metadata: MediaMetadataCompat){
        disposable.unsubscribe()
        disposable = Single.fromCallable { true }
                .filter { AppTheme.isFlat() || AppTheme.isBigImage() || AppTheme.isFullscreen() || AppTheme.isClean() }
                .map { metadata.toPlayerImage() }
                .map { context.getBitmapAsync(it, 200) }
                .subscribeOn(Schedulers.io())
                .map { ImageProcessor(context).processImage(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(colorsPublisher::onNext, Throwable::printStackTrace)
    }

    fun observeImageColors(): Flowable<ImageProcessorResult> = colorsPublisher

    private val progressPublisher = BehaviorSubject.createDefault(0)

    val observeProgress : Observable<Int> = progressPublisher

    fun updateProgress(progress: Int){
        progressPublisher.onNext(progress)
    }

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    fun playerControls(): DisplayableItem {
        val id = when {
            AppTheme.isDefault() -> R.layout.fragment_player_controls
            AppTheme.isFlat() -> R.layout.fragment_player_controls_flat
            AppTheme.isSpotify() -> R.layout.fragment_player_controls_spotify
            AppTheme.isFullscreen() -> R.layout.fragment_player_controls_fullscreen
            AppTheme.isBigImage() -> R.layout.fragment_player_controls_big_image
            AppTheme.isClean() -> R.layout.fragment_player_controls_clean
            else -> throw IllegalStateException("invalid theme")
        }
        return DisplayableItem(id, MediaId.headerId("player controls id"), "")
    }

    val onFavoriteStateChanged: Observable<FavoriteEnum> = observeFavoriteAnimationUseCase.execute()

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()

    fun showLyricsTutorialIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

    override fun onCleared() {
        disposable.unsubscribe()
        insertLyrics.unsubscribe()
    }

    fun getPlaybackSpeed(): Int{
        val speed = musicPrefsUseCase.getPlaybackSpeed()
        return when (speed){
            .5f -> 0
            .8f -> 1
            1f -> 2
            1.2f -> 3
            1.5f -> 4
            2f -> 5
            3f -> 6
            else -> 2
        }
    }

    fun setPlaybackSpeed(itemId: Int) {
        val speed = when (itemId){
            R.id.speed50 -> .5f
            R.id.speed80 -> .8f
            R.id.speed100 -> 1f
            R.id.speed120 -> 1.2f
            R.id.speed150 -> 1.5f
            R.id.speed200 -> 2f
            R.id.speed300 -> 3f
            else -> 1f
        }
        musicPrefsUseCase.setPlaybackSpeed(speed)
    }


}