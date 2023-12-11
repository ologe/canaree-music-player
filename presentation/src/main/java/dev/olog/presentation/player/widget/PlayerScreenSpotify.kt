package dev.olog.presentation.player.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidViewBinding
import dev.olog.media.MediaProvider
import dev.olog.presentation.databinding.PlayerLayoutFlatBinding
import dev.olog.presentation.databinding.PlayerLayoutSpotifyBinding
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.IPlayerAppearanceAdaptiveBehavior
import dev.olog.presentation.player.PlayerFragmentPresenter
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.shared.android.theme.PlayerAppearance

@Composable
fun PlayerScreenSpotify(
    viewModel: PlayerFragmentViewModel,
    presenter: PlayerFragmentPresenter,
    holder: LifecycleHolder,
    navigator: Navigator,
    mediaProvider: MediaProvider,
    playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,
) {
    AndroidViewBinding(PlayerLayoutSpotifyBinding::inflate) {
        bind(
            more = null,
            viewModel = viewModel,
            navigator = navigator
        )

        bindListeners(
            context = root.context,
            repeat = playerControls.repeat,
            shuffle = playerControls.shuffle,
            favorite = null, // TODO??
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
            playerRoot = playerRoot,
            imageSwitcher = imageSwitcherLayout.imageSwitcher,
            title = title,
            artist = artist,
            more = null,
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
            favorite = null,
            seekBar = seekBar,
            repeat = playerControls.repeat,
            shuffle = playerControls.shuffle,
            title = title,
            artist = artist,
            duration = duration,
            imageSwitcher = imageSwitcherLayout.imageSwitcher,
            nowPlaying = null,
            playerAppearance = PlayerAppearance.SPOTIFY,
            mediaProvider = mediaProvider,
            viewModel = viewModel,
            presenter = presenter,
            holder = holder,
        )
    }
}