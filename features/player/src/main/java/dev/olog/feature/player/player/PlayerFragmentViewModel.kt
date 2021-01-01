package dev.olog.feature.player.player

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.Favorite
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.player.R
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.widgets.adaptive.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class PlayerFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    favoriteGateway: FavoriteGateway,
    private val appPrefs: AppPreferencesGateway,
    private val musicPrefs: MusicPreferencesGateway,
    private val tutorialPrefs: TutorialPreferenceGateway
) : ViewModel() {

    private val currentTrackIdPublisher = MutableStateFlow<Long?>(null)
    private val favoritePublisher = MutableStateFlow<Favorite.State?>(null)

    private val processorPublisher = MutableStateFlow<ProcessorColors?>(null)
    private val palettePublisher = MutableStateFlow<PaletteColors?>(null)

    init {
        favoriteGateway.observePlayingTrackFavoriteState()
            .flowOn(Dispatchers.Default)
            .onEach { favoritePublisher.value = it }
            .launchIn(viewModelScope)
    }

    var currentTrackId: Long?
        get() = currentTrackIdPublisher.value
        set(value) {
            currentTrackIdPublisher.value = value
        }

    fun playerControls(): PlayerFragmentModel.Content {
        val ambient = context.playerAppearanceAmbient
        val id = when (ambient.value) {
            PlayerAppearance.DEFAULT -> R.layout.player_layout_default
            PlayerAppearance.FLAT -> R.layout.player_layout_flat
            PlayerAppearance.SPOTIFY -> R.layout.player_layout_spotify
            PlayerAppearance.FULLSCREEN -> R.layout.player_layout_fullscreen
            PlayerAppearance.BIG_IMAGE -> R.layout.player_layout_big_image
            PlayerAppearance.CLEAN -> R.layout.player_layout_clean
            PlayerAppearance.MINI -> R.layout.player_layout_mini
            else -> throw IllegalStateException("invalid theme")
        }
        return PlayerFragmentModel.Content(id)
    }

    val onFavoriteStateChanged: Flow<Favorite.State> = favoritePublisher.filterNotNull()

    val skipToNextVisibility = musicPrefs
            .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefs
            .observeSkipToPreviousVisibility()

    fun showLyricsTutorialIfNeverShown(): Boolean {
        return tutorialPrefs.lyricsTutorial()
    }

    fun getPlaybackSpeed(): Int {
        val speed = musicPrefs.getPlaybackSpeed()
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
        musicPrefs.setPlaybackSpeed(speed)
    }

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return appPrefs.observePlayerControlsVisibility()
    }

    // allow adaptive color on flat appearance
    fun observeProcessorColors(): Flow<ProcessorColors> {

        return processorPublisher
            .filterNotNull()
            .map {
                val playerAppearanceAmbient = context.playerAppearanceAmbient
                if (appPrefs.isAdaptiveColorEnabled || playerAppearanceAmbient.isFlat()) {
                    it
                } else {
                    InvalidProcessColors
                }
            }
            .filter { it is ValidProcessorColors }
            .flowOn(Dispatchers.Default)
    }

    // allow adaptive color on flat appearance
    fun observePaletteColors(): Flow<PaletteColors> {

        return palettePublisher
            .filterNotNull()
            .map {
                val playerAppearanceAmbient = context.playerAppearanceAmbient
                if (appPrefs.isAdaptiveColorEnabled || playerAppearanceAmbient.isFlat() || playerAppearanceAmbient.isSpotify()) {
                    it
                } else {
                    InvalidPaletteColors
                }
            }
            .filter { it is ValidPaletteColors }
            .flowOn(Dispatchers.Default)
    }

    fun updateProcessorColors(palette: ProcessorColors) {
        processorPublisher.value = palette
    }

    fun updatePaletteColors(palette: PaletteColors) {
        palettePublisher.value = palette
    }


}