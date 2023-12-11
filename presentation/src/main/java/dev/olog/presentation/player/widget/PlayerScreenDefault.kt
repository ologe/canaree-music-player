package dev.olog.presentation.player.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import dev.olog.media.MediaProvider
import dev.olog.presentation.databinding.PlayerLayoutDefaultBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.IPlayerAppearanceAdaptiveBehavior
import dev.olog.presentation.player.PlayerFragmentPresenter
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun PlayerScreenDefault(
    viewModel: PlayerFragmentViewModel,
    presenter: PlayerFragmentPresenter,
    holder: LifecycleHolder,
    navigator: Navigator,
    mediaProvider: MediaProvider,
    playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,
) {
    AndroidViewBinding(PlayerLayoutDefaultBinding::inflate) {
        bind(
            more = more,
            viewModel = viewModel,
            navigator = navigator
        )

        bindListeners(
            context = root.context,
            repeat = playerControls.repeat,
            shuffle = playerControls.shuffle,
            favorite = playerToolbar.favorite,
            lyrics = playerToolbar.lyrics,
            next = playerControls.next,
            playPause = playerControls.playPause,
            previous = playerControls.previous,
            replay = playerControls.replay,
            replay30 = playerControls.replay30,
            forward = playerControls.forward,
            forward30 = playerControls.forward30,
            playbackSpeed = playerToolbar.playbackSpeed,
            seekBar = seekBar,
            bookmark = bookmark,
            mediaProvider = mediaProvider,
            navigator = navigator,
            viewModel = viewModel,

            rootView = root,
            playerRoot = null,
            imageSwitcher = imageSwitcherLayout.imageSwitcher,
            title = title,
            artist = artist,
            more = more,
            presenter = presenter,
            playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
            holder = holder,
        )

        bindPlayerControls(
            rootView = root,
            next = playerControls.next,
            previous = playerControls.previous,
            playPause = playerControls.playPause,
            volume = playerToolbar.volume,
            swipeableView = swipeableView,
            favorite = playerToolbar.favorite,
            seekBar = seekBar,
            repeat = playerControls.repeat,
            shuffle = playerControls.shuffle,
            title = title,
            artist = artist,
            duration = duration,
            imageSwitcher = imageSwitcherLayout.imageSwitcher,
            nowPlaying = playerToolbar.nowPlaying,
            playerAppearance = PlayerAppearance.DEFAULT,
            mediaProvider = mediaProvider,
            viewModel = viewModel,
            presenter = presenter,
            holder = holder,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {

        }
    }
}