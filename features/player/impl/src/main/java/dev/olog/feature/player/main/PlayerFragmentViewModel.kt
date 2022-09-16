package dev.olog.feature.player.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.player.R
import dev.olog.platform.theme.PlayerAppearance
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.ui.model.DisplayableHeader
import dev.olog.ui.model.DisplayableItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    favoriteGateway: FavoriteGateway,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) : ViewModel() {

    private val currentTrackIdPublisher = MutableStateFlow<Long?>(null)

    private val favoriteLiveData = MutableLiveData<Boolean>()

    init {
        favoriteGateway.observePlayingFavorite()
            .onEach { favoriteLiveData.value = it }
            .launchIn(viewModelScope)
    }

    fun getCurrentTrackId(): Long? = currentTrackIdPublisher.value

    fun updateCurrentTrackId(trackId: Long) {
        currentTrackIdPublisher.value = trackId
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

    val onFavoriteStateChanged: LiveData<Boolean> = favoriteLiveData

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