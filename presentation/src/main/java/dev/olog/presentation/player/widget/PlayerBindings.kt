package dev.olog.presentation.player.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.asLiveData
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerMetadata
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.media.model.PlayerState
import dev.olog.media.widget.CustomSeekBar
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.IPlayerAppearanceAdaptiveBehavior
import dev.olog.presentation.player.PlayerFragmentPresenter
import dev.olog.presentation.player.PlayerFragmentViewModel
import dev.olog.presentation.player.rotate
import dev.olog.presentation.player.volume.PlayerVolumeFragment
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.presentation.widgets.LottieFavorite
import dev.olog.presentation.widgets.RepeatButton
import dev.olog.presentation.widgets.ShuffleButton
import dev.olog.presentation.widgets.imageview.PlayerImageView
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import dev.olog.presentation.widgets.switcher.CustomViewSwitcher
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.android.extensions.filter
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.extensions.fragmentTransaction
import dev.olog.shared.android.extensions.map
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.theme.PlayerAppearance
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.theme.isBigImage
import dev.olog.shared.android.theme.isFullscreen
import dev.olog.shared.android.theme.isMini
import dev.olog.shared.android.theme.isSpotify
import dev.olog.shared.widgets.AnimatedImageView
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import kotlinx.coroutines.flow.filter


fun bind(
    more: View?,
    viewModel: PlayerFragmentViewModel,
    navigator: Navigator,
) {
    more?.setOnClickListener { view ->
        try {
            val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
            navigator.toDialog(mediaId, view)
        } catch (ex: NullPointerException){
            ex.printStackTrace()
        }
    }
}

fun bindListeners(
    context: Context,
    playerRoot: View?,
    rootView: View,
    repeat: RepeatButton,
    shuffle: ShuffleButton,
    favorite: LottieFavorite?,
    lyrics: ImageView,
    next: View,
    playPause: AnimatedPlayPauseImageView,
    previous: View,
    replay: View,
    replay30: View,
    forward: View,
    forward30: View,
    playbackSpeed: View,
    seekBar: CustomSeekBar,
    bookmark: TextView,
    imageSwitcher: CustomViewSwitcher?,
    title: TextView,
    artist: TextView,
    more: ImageView?,
    mediaProvider: MediaProvider,
    navigator: Navigator,
    viewModel: PlayerFragmentViewModel,
    presenter: PlayerFragmentPresenter,
    playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,
    holder: LifecycleHolder,
) {
    repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
    shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
    favorite?.setOnClickListener {
        favorite.toggleFavorite()
        mediaProvider.togglePlayerFavorite()
    }
    lyrics.setOnClickListener { navigator.toOfflineLyrics() }
    next.setOnClickListener { mediaProvider.skipToNext() }
    playPause.setOnClickListener { mediaProvider.playPause() }
    previous.setOnClickListener { mediaProvider.skipToPrevious() }

    replay.setOnClickListener {
        it.rotate(-30f)
        mediaProvider.replayTenSeconds()
    }

    replay30.setOnClickListener {
        it.rotate(-50f)
        mediaProvider.replayThirtySeconds()
    }

    forward.setOnClickListener {
        it.rotate(30f)
        mediaProvider.forwardTenSeconds()
    }

    forward30.setOnClickListener {
        it.rotate(50f)
        mediaProvider.forwardThirtySeconds()
    }

    playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(it, viewModel) }

    seekBar.setListener(
        onProgressChanged = {
            bookmark.text = TextUtils.formatMillis(it)
        }, onStartTouch = {

        }, onStopTouch = {
            mediaProvider.seekTo(it.toLong())
        }
    )

    imageSwitcher?.let {
        it.observeProcessorColors()
            .asLiveData()
            .subscribe(holder, presenter::updateProcessorColors)
        it.observePaletteColors()
            .asLiveData()
            .subscribe(holder, presenter::updatePaletteColors)
    }
    rootView.findViewById<PlayerImageView>(R.id.miniCover)?.let {
        it.observeProcessorColors()
            .asLiveData()
            .subscribe(holder, presenter::updateProcessorColors)
        it.observePaletteColors()
            .asLiveData()
            .subscribe(holder, presenter::updatePaletteColors)
    }


    playerAppearanceAdaptiveBehavior(
        context,
        playerRoot,
        shuffle,
        repeat,
        title,
        artist,
        seekBar,
        playPause,
        more,
        lyrics,
        holder,
        presenter
    )
}

fun bindPlayerControls(
    rootView: View,
    next: AnimatedImageView,
    previous: AnimatedImageView,
    playPause: AnimatedPlayPauseImageView,
    volume: View?,
    swipeableView: SwipeableView?,
    favorite: LottieFavorite?,
    seekBar: CustomSeekBar,
    repeat: RepeatButton,
    shuffle: ShuffleButton,
    title: TextView,
    artist: TextView,
    duration: TextView,
    imageSwitcher: CustomViewSwitcher?,
    nowPlaying: View?,
    playerAppearance: PlayerAppearance,
    mediaProvider: MediaProvider,
    viewModel: PlayerFragmentViewModel,
    presenter: PlayerFragmentPresenter,
    holder: LifecycleHolder,
) {
    val context = rootView.context
    if (!playerAppearance.isSpotify() && !playerAppearance.isBigImage()){
        next.setDefaultColor()
        previous.setDefaultColor()
        playPause.setDefaultColor()
    }

    mediaProvider.observeMetadata()
        .subscribe(holder) {
            viewModel.updateCurrentTrackId(it.id)

            updateMetadata(
                context = context,
                rootView = rootView,
                title = title,
                artist = artist,
                duration = duration,
                seekBar = seekBar,
                metadata = it
            )
            updateImage(
                rootView = rootView,
                imageSwitcher = imageSwitcher,
                metadata = it
            )
        }

    volume?.setOnClickListener {
        val outLocation = intArrayOf(0, 0)
        it.getLocationInWindow(outLocation)
        // TODO
        val yLocation = (outLocation[1]/* - StatusBarviewHeight*/).toFloat()
        (context.findInContext<FragmentActivity>()).fragmentTransaction {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(android.R.id.content, PlayerVolumeFragment.newInstance(
                R.layout.player_volume,
                yLocation
            ), PlayerVolumeFragment.TAG)
            addToBackStack(PlayerVolumeFragment.TAG)
        }
    }

    mediaProvider.observePlaybackState()
        .subscribe(holder) {
            onPlaybackStateChanged(
                nowPlaying = nowPlaying,
                imageSwitcher = imageSwitcher,
                playbackState = it
            )
        }

    mediaProvider.observePlaybackState()
        .subscribe(holder) { seekBar.onStateChanged(it) }

    mediaProvider.observeRepeat()
        .subscribe(holder, repeat::cycle)

    mediaProvider.observeShuffle()
        .subscribe(holder, shuffle::cycle)

    swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener {
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

    viewModel.onFavoriteStateChanged
        .subscribe(holder) {
            favorite?.onNextState(it)
        }

    viewModel.skipToNextVisibility
        .asLiveData()
        .subscribe(holder, next::updateVisibility)

    viewModel.skipToPreviousVisibility
        .asLiveData()
        .subscribe(holder, previous::updateVisibility)

    presenter.observePlayerControlsVisibility()
        .filter { !playerAppearance.isFullscreen()
            && !playerAppearance.isMini()
            && !playerAppearance.isSpotify()
            && !playerAppearance.isBigImage()
        }
        .asLiveData()
        .subscribe(holder) { visible ->
            rootView.findViewById<View>(R.id.playerControls)
                ?.findViewById<View>(R.id.player)
                ?.toggleVisibility(visible, true)
        }


    mediaProvider.observePlaybackState()
        .filter { it.isSkipTo }
        .map { it.state == PlayerState.SKIP_TO_NEXT }
        .subscribe(holder) {
            animateSkipTo(
                context = context,
                next = next,
                previous = previous,
                toNext = it
            )
        }

    mediaProvider.observePlaybackState()
        .filter { it.isPlayOrPause }
        .map { it.state }
        .distinctUntilChanged()
        .subscribe(holder) { state ->
            when (state) {
                PlayerState.PLAYING -> playAnimation(context, playPause)
                PlayerState.PAUSED -> pauseAnimation(context, playPause)
                else -> throw IllegalArgumentException("invalid state $state")
            }
        }
}

private fun animateSkipTo(
    context: Context,
    next: AnimatedImageView,
    previous: AnimatedImageView,
    toNext: Boolean
) {
    val hasSlidingPanel = (context).findInContext<HasSlidingPanel>()
    if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

    if (toNext) {
        next.playAnimation()
    } else {
        previous.playAnimation()
    }
}

private fun playAnimation(
    context: Context,
    playPause: AnimatedPlayPauseImageView,
) {
    val hasSlidingPanel = (context.findInContext<HasSlidingPanel>())
    val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
    playPause.animationPlay(isPanelExpanded)
}

private fun pauseAnimation(
    context: Context,
    playPause: AnimatedPlayPauseImageView,
) {
    val hasSlidingPanel = (context.findInContext<HasSlidingPanel>())
    val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
    playPause.animationPause(isPanelExpanded)
}

private fun onPlaybackStateChanged(
    nowPlaying: View?,
    imageSwitcher: CustomViewSwitcher?,
    playbackState: PlayerPlaybackState,
) {
    val isPlaying = playbackState.isPlaying

    if (isPlaying || playbackState.isPaused) {
        nowPlaying?.isActivated = isPlaying
        imageSwitcher?.setChildrenActivated(isPlaying)
    }
}

private fun updateMetadata(
    context: Context,
    rootView: View,
    title: TextView,
    artist: TextView,
    duration: TextView,
    seekBar: CustomSeekBar,
    metadata: PlayerMetadata
) {
    if (context.hasPlayerAppearance().isFlat()){
        // WORKAROUND, all caps attribute is not working for some reason
        title.text = metadata.title.toUpperCase()
    } else {
        title.text = metadata.title
    }
    artist.text = metadata.artist

    val readableDuration = metadata.readableDuration
    duration.text = readableDuration
    seekBar.max = metadata.duration.toInt()

    val isPodcast = metadata.isPodcast
    rootView.findViewById<ViewGroup>(R.id.podcast_controls).toggleVisibility(isPodcast, true)
}

private fun updateImage(
    rootView: View,
    imageSwitcher: CustomViewSwitcher?,
    metadata: PlayerMetadata
) {
    imageSwitcher?.loadImage(metadata)
    rootView.findViewById<PlayerImageView>(R.id.miniCover)?.loadImage(metadata.mediaId)
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