package dev.olog.presentation.player.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.IPlayerAppearanceAdaptiveBehavior
import dev.olog.presentation.player.PlayerFragmentPresenter
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.compose.theme.Theme

class LifecycleHolder : LifecycleOwner {
    private val registry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() = registry

    fun attach() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
    fun detach() {
        // TODO check
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}

@Composable
fun PlayerScreen(
    appearance: PlayerAppearance,
    viewModel: PlayerFragmentViewModel,
    presenter: PlayerFragmentPresenter,
    navigator: Navigator,
    mediaProvider: MediaProvider,
    playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,

) {
    val holder = remember { LifecycleHolder() }
    DisposableEffect(Unit) {
        holder.attach()
        onDispose {
            holder.detach()
        }
    }

    Box(Modifier.background(Theme.colors.background)) {
        when (appearance) {
            PlayerAppearance.DEFAULT -> {
                PlayerScreenDefault(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.FLAT -> {
                PlayerScreenFlat(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.SPOTIFY -> {
                PlayerScreenSpotify(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.FULLSCREEN -> {
                PlayerScreenFullscreen(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.BIG_IMAGE -> {
                PlayerScreenBigImage(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.CLEAN -> {
                PlayerScreenClean(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
            PlayerAppearance.MINI -> {
                PlayerScreenMini(
                    viewModel = viewModel,
                    presenter = presenter,
                    holder = holder,
                    navigator = navigator,
                    mediaProvider = mediaProvider,
                    playerAppearanceAdaptiveBehavior = playerAppearanceAdaptiveBehavior,
                )
            }
        }
    }
}