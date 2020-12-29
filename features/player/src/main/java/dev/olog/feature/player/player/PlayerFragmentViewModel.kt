package dev.olog.feature.player.player

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.player.R
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.playerAppearanceAmbient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class PlayerFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) : ViewModel() {

    private val currentTrackIdPublisher = MutableStateFlow<Long?>(null)

    private val favoritePublisher = MutableStateFlow<FavoriteEnum?>(null)

    init {
        observeFavoriteAnimationUseCase()
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

    val onFavoriteStateChanged: Flow<FavoriteEnum> = favoritePublisher.filterNotNull()

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