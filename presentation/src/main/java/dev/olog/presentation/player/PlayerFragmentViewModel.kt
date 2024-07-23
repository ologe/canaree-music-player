package dev.olog.presentation.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.widgets.imageview.AdaptiveImageHelper
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.theme.isFlat
import dev.olog.shared.android.theme.isSpotify
import dev.olog.shared.widgets.adaptive.PaletteColors
import dev.olog.shared.widgets.adaptive.ProcessorColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val adapterImageHelper: AdaptiveImageHelper,
) : ViewModel() {

    private val currentTrackIdPublisher = MutableStateFlow<Long?>(null)
    private val favoriteLiveData = MutableStateFlow<FavoriteEnum?>(null)

    init {
        viewModelScope.launch {
            observeFavoriteAnimationUseCase()
                .flowOn(Dispatchers.Default)
                .collect { favoriteLiveData.value = it }
        }
    }

    fun getCurrentTrackId() = currentTrackIdPublisher.value

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.value = trackId
    }

    val onFavoriteStateChanged: Flow<FavoriteEnum> = favoriteLiveData.filterNotNull()

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()

    fun observePlayerControlsVisibility(): Flow<Boolean> {
        return presentationPrefs.observePlayerControlsVisibility()
    }

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

    fun observeProcessorColors(): Flow<ProcessorColors?> {
        return combine(
            adapterImageHelper.observeProcessorColors(),
            context.hasPlayerAppearance().observePlayerAppearance(),
            presentationPrefs.observeAdaptiveColorEnabled(),
        ) { colors, appearance, isAdaptive ->
            if (isAdaptive || appearance.isFlat()) {
                colors
            } else {
                null
            }
        }.flowOn(Dispatchers.Default)
    }

    fun observePaletteColors(): Flow<PaletteColors?> {
        return combine(
            adapterImageHelper.observePaletteColors(),
            context.hasPlayerAppearance().observePlayerAppearance(),
            presentationPrefs.observeAdaptiveColorEnabled(),
        ) { palette, appearance, isAdaptive ->
            if (isAdaptive || appearance.isFlat() || appearance.isSpotify()) {
                palette
            } else {
                null
            }
        }.flowOn(Dispatchers.Default)
    }


}