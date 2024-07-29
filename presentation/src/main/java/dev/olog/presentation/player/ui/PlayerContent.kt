package dev.olog.presentation.player.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerMetadata
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.media.model.PlayerState
import dev.olog.presentation.R
import dev.olog.presentation.databinding.PlayerLayoutBigImageBinding
import dev.olog.presentation.databinding.PlayerLayoutCleanBinding
import dev.olog.presentation.databinding.PlayerLayoutDefaultBinding
import dev.olog.presentation.databinding.PlayerLayoutFlatBinding
import dev.olog.presentation.databinding.PlayerLayoutFullscreenBinding
import dev.olog.presentation.databinding.PlayerLayoutMiniBinding
import dev.olog.presentation.databinding.PlayerLayoutSpotifyBinding
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.presentation.player.rememberIPlayerAppearanceAdaptiveBehavior
import dev.olog.presentation.player.rotate
import dev.olog.presentation.player.volume.PlayerVolumeFragment
import dev.olog.presentation.utils.isExpanded
import dev.olog.presentation.widgets.StatusBarView
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.fragmentTransaction
import dev.olog.shared.android.extensions.setHeight
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance

@Composable
fun PlayerContent(
    itemView: View,
    navigator: Navigator,
    viewModel: PlayerFragmentViewModel,
) {
    val context = LocalContext.current
    val hasPlayerAppearance = remember(context) { context.hasPlayerAppearance() }
    val playerAppearance by hasPlayerAppearance.observePlayerAppearance().collectAsState()
    val mediaProvider = remember(context) { context.findInContext<MediaProvider>() }

    // TODO replace with collectAsStateWithLifecycle
    val metadata by mediaProvider.observeMetadata().collectAsState()
    val playbackState by mediaProvider.observePlaybackState().collectAsState()
    val repeatMode by mediaProvider.observeRepeat().collectAsState()
    val shuffleMode by mediaProvider.observeShuffle().collectAsState()
    val favoriteState by viewModel.onFavoriteStateChanged.collectAsState(null)
    val skipToNextVisibility by viewModel.skipToNextVisibility.collectAsState(null)
    val skipToPreviousVisibility by viewModel.skipToPreviousVisibility.collectAsState(null)
    val playerControlsVisibility by viewModel.observePlayerControlsVisibility().collectAsState(null)
    val appearanceBehavior = rememberIPlayerAppearanceAdaptiveBehavior(playerAppearance)
    val processorColors by viewModel.observeProcessorColors().collectAsState(null)
    val paletteColors by viewModel.observePaletteColors().collectAsState(null)

    LaunchedEffect(metadata?.id) {
        metadata?.id?.let { viewModel.updateCurrentTrackId(it) }
    }

    fun update(bindings: PlayerBindings) {
        if (playerAppearance != PlayerAppearance.SPOTIFY && playerAppearance != PlayerAppearance.BIG_IMAGE){
            bindings.next.setDefaultColor()
            bindings.previous.setDefaultColor()
            bindings.playPause.setDefaultColor()
        }

        setupListeners(
            bindings = bindings,
            mediaProvider = mediaProvider,
            navigator = navigator,
            viewModel = viewModel,
        )
        updateMetadata(
            bindings = bindings,
            playerAppearance = playerAppearance,
            metadata = metadata,
        )
        updateImage(
            bindings = bindings,
            metadata = metadata,
        )

        updatePlaybackState(
            bindings = bindings,
            playbackState = playbackState,
        )

        repeatMode?.let { bindings.repeat.cycle(it) }
        shuffleMode?.let { bindings.shuffle.cycle(it) }

        favoriteState?.let { bindings.favorite.onNextState(it) }
        skipToNextVisibility?.let { bindings.next.updateVisibility(it) }
        skipToPreviousVisibility?.let { bindings.previous.updateVisibility(it) }

        playerControlsVisibility?.let {
            if (playerAppearance != PlayerAppearance.FULLSCREEN &&
                playerAppearance != PlayerAppearance.MINI &&
                playerAppearance != PlayerAppearance.SPOTIFY &&
                playerAppearance != PlayerAppearance.BIG_IMAGE) {
                bindings.playerControls?.isVisible = it
            }
        }

        appearanceBehavior(
            bindings = bindings,
            processorColors = processorColors,
            paletteColors = paletteColors,
        )
    }

    when (playerAppearance) {
        PlayerAppearance.DEFAULT -> DefaultPlayerContent(itemView, ::update)
        PlayerAppearance.FLAT -> FlatPlayerContent(itemView, ::update)
        PlayerAppearance.SPOTIFY -> SpotifyPlayerContent(itemView, ::update)
        PlayerAppearance.FULLSCREEN -> FullscreenPlayerContent(itemView, ::update)
        PlayerAppearance.BIG_IMAGE -> BigImagePlayerContent(itemView, ::update)
        PlayerAppearance.CLEAN -> CleanPlayerContent(itemView, ::update)
        PlayerAppearance.MINI -> MiniPlayerContent(itemView, ::update)
    }
}

@Composable
private fun DefaultPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutDefaultBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutDefaultBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun FlatPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutFlatBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutFlatBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun SpotifyPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutSpotifyBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutSpotifyBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun FullscreenPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)

    AndroidView(
        factory = { PlayerLayoutFullscreenBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxSize(),
    ) {
        val binding = PlayerLayoutFullscreenBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun BigImagePlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutBigImageBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutBigImageBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun CleanPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutCleanBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutCleanBinding.bind(it)
        update(binding.bindPlayer())
    }
}

@Composable
private fun MiniPlayerContent(
    itemView: View,
    update: (PlayerBindings) -> Unit
) {
    itemView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
    AndroidView(
        factory = { PlayerLayoutMiniBinding.inflate(LayoutInflater.from(it)).root },
        modifier = Modifier.fillMaxWidth()
    ) {
        val binding = PlayerLayoutMiniBinding.bind(it)
        update(binding.bindPlayer())
    }
}

private fun setupListeners(
    bindings: PlayerBindings,
    mediaProvider: MediaProvider,
    navigator: Navigator,
    viewModel: PlayerFragmentViewModel,
) {
    bindings.repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
    bindings.shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
    bindings.favorite.setOnClickListener {
        bindings.favorite.toggleFavorite()
        mediaProvider.togglePlayerFavorite()
    }
    bindings.lyrics.setOnClickListener { navigator.toOfflineLyrics() }
    bindings.next.setOnClickListener { mediaProvider.skipToNext() }
    bindings.playPause.setOnClickListener { mediaProvider.playPause() }
    bindings.previous.setOnClickListener { mediaProvider.skipToPrevious() }

    bindings.replay.setOnClickListener {
        it.rotate(-30f)
        mediaProvider.replayTenSeconds()
    }

    bindings.replay30.setOnClickListener {
        it.rotate(-50f)
        mediaProvider.replayThirtySeconds()
    }

    bindings.forward.setOnClickListener {
        it.rotate(30f)
        mediaProvider.forwardTenSeconds()
    }

    bindings.forward30.setOnClickListener {
        it.rotate(50f)
        mediaProvider.forwardThirtySeconds()
    }

    bindings.playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(it, viewModel) }

    bindings.seekBar.setListener(
        onProgressChanged = {
            bindings.bookmark.text = TextUtils.formatMillis(it)
        }, onStartTouch = {

        }, onStopTouch = {
            mediaProvider.seekTo(it.toLong())
        }
    )

    bindings.more.setOnClickListener {
        val mediaId = MediaId.songId(viewModel.getCurrentTrackId() ?: return@setOnClickListener)
        navigator.toDialog(mediaId, it)
    }

    bindings.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener {
        override fun onSwipedLeft() {
            mediaProvider.skipToNext()
        }

        override fun onSwipedRight() {
            mediaProvider.skipToPrevious()
        }

        override fun onClick() {
            mediaProvider.playPause()
        }

        override fun onLeftEdgeClick() {
            mediaProvider.skipToPrevious()
        }

        override fun onRightEdgeClick() {
            mediaProvider.skipToNext()
        }
    })

    bindings.volume?.setOnClickListener {
        val outLocation = intArrayOf(0, 0)
        it.getLocationInWindow(outLocation)
        val yLocation = (outLocation[1] - StatusBarView.viewHeight).toFloat()
        (it.context.findInContext<FragmentActivity>()).fragmentTransaction {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, PlayerVolumeFragment.newInstance(yLocation), PlayerVolumeFragment.TAG)
            addToBackStack(PlayerVolumeFragment.TAG)
        }
    }
}

private fun updateMetadata(
    bindings: PlayerBindings,
    playerAppearance: PlayerAppearance,
    metadata: PlayerMetadata?,
) {
    if (metadata == null) return
    if (playerAppearance == PlayerAppearance.FLAT) {
        // WORKAROUND, all caps attribute is not working for some reason
        bindings.title.text = metadata.title.toUpperCase()
    } else {
        bindings.title.text = metadata.title
    }
    bindings.artist.text = metadata.artist

    bindings.duration.text = metadata.readableDuration
    bindings.seekBar.max = metadata.duration.toInt()

    val isPodcast = metadata.isPodcast
    bindings.podcastControls.toggleVisibility(isPodcast, true)
}

private fun updateImage(
    bindings: PlayerBindings,
    metadata: PlayerMetadata?,
) {
    if (metadata == null) return
    bindings.imageSwitcher?.loadImage(metadata)
    bindings.miniCover?.loadImage(metadata.mediaId)
}

private fun updatePlaybackState(
    bindings: PlayerBindings,
    playbackState: PlayerPlaybackState?,
) {
    if (playbackState == null) return
    val isPlaying = playbackState.isPlaying

    if (isPlaying || playbackState.isPaused) {
        bindings.nowPlaying?.animateNowPlaying(isPlaying)
        bindings.imageSwitcher?.updateChildren(isPlaying)
    }

    bindings.bookmark.text = TextUtils.formatMillis(playbackState.bookmark)
    bindings.seekBar.onStateChanged(playbackState)

//    if (playbackState.isSkipTo) {
//        when (playbackState.state) {
//            PlayerState.SKIP_TO_NEXT,
//            PlayerState.SKIP_TO_PREVIOUS -> {
//                animateSkipTo(bindings, playbackState.state == PlayerState.SKIP_TO_NEXT)
//            }
//        }
//    }

    if (playbackState.isPlayOrPause) {
        when (playbackState.state) {
            PlayerState.PLAYING -> playAnimation(bindings)
            PlayerState.PAUSED -> pauseAnimation(bindings)
            PlayerState.SKIP_TO_NEXT,
            PlayerState.SKIP_TO_PREVIOUS -> {}
        }
    }
}

// TODO find a fix
//private fun animateSkipTo(
//    bindings: PlayerBindings,
//    toNext: Boolean
//) {
//    val context = bindings.next.context
//    val hasSlidingPanel = context.findInContext<HasSlidingPanel>()
//    if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return
//
//    if (toNext) {
//        bindings.next.playAnimation()
//    } else {
//        bindings.previous.playAnimation()
//    }
//}

private fun playAnimation(bindings: PlayerBindings) {
    val context = bindings.playPause.context
    val hasSlidingPanel = context.findInContext<HasSlidingPanel>()
    val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
    bindings.playPause.animationPlay(isPanelExpanded)
}

private fun pauseAnimation(bindings: PlayerBindings) {
    val context = bindings.playPause.context
    val hasSlidingPanel = context.findInContext<HasSlidingPanel>()
    val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
    bindings.playPause.animationPause(isPanelExpanded)
}

private fun openPlaybackSpeedPopup(
    view: View,
    viewModel: PlayerFragmentViewModel,
) {
    val popup = PopupMenu(view.context, view)
    popup.inflate(R.menu.dialog_playback_speed)
    popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
    popup.setOnMenuItemClickListener {
        viewModel.setPlaybackSpeed(it.itemId)
        true
    }
    popup.show()
}