package dev.olog.presentation.player

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.widgets.adaptive.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val processorPublisher = BroadcastChannel<ProcessorColors>(Channel.CONFLATED)
    private val palettePublisher = BroadcastChannel<PaletteColors>(Channel.CONFLATED)
    private val currentTrackIdPublisher = BroadcastChannel<Long>(Channel.CONFLATED)

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> = processorPublisher
        .asFlow()
        .map {
            val hasPlayerAppearance = context.hasPlayerAppearance()
            if (presentationPrefs.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat()) {
                it
            } else {
                InvalidProcessColors
            }
        }
        .filter { it is ValidProcessorColors }
        .flowOn(Dispatchers.Default)

    // allow adaptive color on flat appearance
    fun observePaletteColors(): Flow<PaletteColors> = palettePublisher
        .asFlow()
        .map {
            val hasPlayerAppearance = context.hasPlayerAppearance()
            if (presentationPrefs.isAdaptiveColorEnabled() || hasPlayerAppearance.isFlat() || hasPlayerAppearance.isSpotify()) {
                it
            } else {
                InvalidPaletteColors
            }
        }
        .filter { it is ValidPaletteColors }
        .flowOn(Dispatchers.Default)

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.offer(palette)
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.offer(palette)
    }

    fun getCurrentTrackId() = currentTrackIdPublisher.openSubscription().poll()!!

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.offer(trackId)
    }

    val footerLoadMore : DisplayableItem = DisplayableHeader(
            type = R.layout.item_mini_queue_load_more,
            mediaId = MediaId.headerId("load more"),
            title = ""
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
        return DisplayableHeader(
            type = id,
            mediaId = MediaId.headerId("player controls id"),
            title = ""
        )
    }

    val onFavoriteStateChanged: Flow<FavoriteEnum> = observeFavoriteAnimationUseCase()

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()

    fun showLyricsTutorialIfNeverShown(): Boolean {
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