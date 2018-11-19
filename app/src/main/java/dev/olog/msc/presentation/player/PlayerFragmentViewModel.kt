package dev.olog.msc.presentation.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.msc.R
import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.widget.image.view.player.*
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val appPreferencesUseCase: AppPreferencesGateway,
        private val musicPrefsUseCase: MusicPreferencesGateway,
        private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val processorPublisher = BehaviorSubject.create<ProcessorColors>()
    private val palettePublisher = BehaviorSubject.create<PaletteColors>()

    fun observeProcessorColors(): Observable<ProcessorColors> = processorPublisher
            .map {
                if (appPreferencesUseCase.isAdaptiveColorEnabled()){
                    it
                } else {
                    InvalidProcessColors
                }
            }
            .filter { it is ValidProcessorColors }
            .observeOn(AndroidSchedulers.mainThread())

    fun observePaletteColors(): Observable<PaletteColors> = palettePublisher
            .map {
                if (appPreferencesUseCase.isAdaptiveColorEnabled()){
                    it
                } else {
                    InvalidPaletteColors
                }
            }
            .filter { it is ValidPaletteColors }
            .observeOn(AndroidSchedulers.mainThread())

    fun updateProcessorColors(palette: ProcessorColors){
        processorPublisher.onNext(palette)
    }

    fun updatePaletteColors(palette: PaletteColors){
        palettePublisher.onNext(palette)
    }

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

    private val progressPublisher = BehaviorSubject.createDefault(0)

    val observeProgress : Observable<Int> = progressPublisher

    fun updateProgress(progress: Int){
        progressPublisher.onNext(progress)
    }

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    fun playerControls(): DisplayableItem {
        val id = when {
            AppTheme.isDefaultTheme() -> R.layout.fragment_player_controls
            AppTheme.isFlatTheme() -> R.layout.fragment_player_controls_flat
            AppTheme.isSpotifyTheme() -> R.layout.fragment_player_controls_spotify
            AppTheme.isFullscreenTheme() -> R.layout.fragment_player_controls_fullscreen
            AppTheme.isBigImageTheme() -> R.layout.fragment_player_controls_big_image
            AppTheme.isCleanTheme() -> R.layout.fragment_player_controls_clean
            AppTheme.isMiniTheme() -> R.layout.fragment_player_controls_mini
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