package dev.olog.feature.player

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.favorite.FavoriteState
import dev.olog.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.themeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

internal class PlayerFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
    private val musicPrefsUseCase: MusicPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    schedulers: Schedulers

) : ViewModel() {

    val onFavoriteStateChanged: Flow<FavoriteState> = observeFavoriteAnimationUseCase()
        .flowOn(schedulers.cpu)

    val skipToNextVisibility = musicPrefsUseCase
        .observeSkipToNextVisibility()

    val skipToPreviousVisibility = musicPrefsUseCase
        .observeSkipToPreviousVisibility()

    val footerLoadMore : DisplayableItem =
        DisplayableHeader(
            type = R.layout.item_mini_queue_load_more,
            mediaId = headerId("load more"),
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
        }
        return DisplayableHeader(
            type = id,
            mediaId = headerId("player controls id"),
            title = ""
        )
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


}