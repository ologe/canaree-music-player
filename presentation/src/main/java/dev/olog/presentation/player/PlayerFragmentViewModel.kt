package dev.olog.presentation.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteState
import dev.olog.core.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.themeManager
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class PlayerFragmentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    schedulers: Schedulers

) : ViewModel() {

    private val currentTrackIdPublisher = ConflatedBroadcastChannel<Long>()

    private val favoriteLiveData = MutableLiveData<FavoriteState>()

    init {
        observeFavoriteAnimationUseCase()
            .flowOn(schedulers.cpu)
            .onEach { favoriteLiveData.value = it }
            .launchIn(viewModelScope)
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
        val id = when (context.themeManager.playerAppearance) {
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

    val onFavoriteStateChanged: LiveData<FavoriteState> = favoriteLiveData

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