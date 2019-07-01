package dev.olog.presentation.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.R
import dev.olog.core.interactor.ObserveFavoriteAnimationUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.theme.PlayerAppearance
import dev.olog.shared.theme.hasPlayerAppearance
import dev.olog.shared.widgets.adaptive.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val appPreferencesUseCase: AppPreferencesGateway,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val processorPublisher = BehaviorSubject.create<ProcessorColors>()
    private val palettePublisher = BehaviorSubject.create<PaletteColors>()

    fun observeProcessorColors(): Observable<ProcessorColors> = processorPublisher
            .map {
                if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                    it
                } else {
                    InvalidProcessColors
                }
            }
            .filter { it is ValidProcessorColors }
            .observeOn(AndroidSchedulers.mainThread())

    fun observePaletteColors(): Observable<PaletteColors> = palettePublisher
            .map {
                if (appPreferencesUseCase.isAdaptiveColorEnabled()) {
                    it
                } else {
                    InvalidPaletteColors
                }
            }
            .filter { it is ValidPaletteColors }
            .observeOn(AndroidSchedulers.mainThread())

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.onNext(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.onNext(palette)
    }

    private val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    fun getCurrentTrackId() = currentTrackIdPublisher.value!!

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.onNext(trackId)
    }

    val footerLoadMore = DisplayableItem(
            R.layout.item_mini_queue_load_more,
            MediaId.headerId("load more"),
            ""
    )

    fun playerControls(): DisplayableItem {
        val hasPlayerAppearance = context.hasPlayerAppearance()
        val id = when (hasPlayerAppearance.playerAppearance()) {
            PlayerAppearance.DEFAULT -> R.layout.player_layout_default
            PlayerAppearance.FLAT -> R.layout.player_layout_flat
            PlayerAppearance.SPOTIFY -> R.layout.player_layout_spotify
            PlayerAppearance.FULLSCREEN -> R.layout.player_layout_fullscreen
            PlayerAppearance.BIG_IMAGE -> R.layout.player_layout_big_image
            PlayerAppearance.CLEAN -> R.layout.player_layout_clean
            PlayerAppearance.MINI -> R.layout.player_layout_mini
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

    fun getPlaybackSpeed(): Int {
        val speed = musicPrefsUseCase.getPlaybackSpeed()
        return when (speed) {
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
        val speed = when (itemId) {
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