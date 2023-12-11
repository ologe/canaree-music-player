package dev.olog.presentation.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()

    private val favoriteLiveData = MutableLiveData<FavoriteEnum>()

    init {
        viewModelScope.launch {
            observeFavoriteAnimationUseCase()
                .flowOn(Dispatchers.Default)
                .collect { favoriteLiveData.value = it }
        }
    }

    fun getCurrentTrackId() = currentTrackIdPublisher.openSubscription().tryReceive().getOrNull()!!

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.trySend(trackId)
    }

    val onFavoriteStateChanged: LiveData<FavoriteEnum> = favoriteLiveData

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