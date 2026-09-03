package dev.olog.presentation.player

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerMetadata
import dev.olog.media.model.PlayerPlaybackState
import dev.olog.media.model.PlayerState
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.volume.PlayerVolumeFragment
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.presentation.widgets.StatusBarView
import dev.olog.presentation.widgets.imageview.PlayerImageView
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.swap
import kotlinx.coroutines.flow.filter
import dev.olog.presentation.widgets.LottieFavorite
import dev.olog.presentation.widgets.RepeatButton
import dev.olog.presentation.widgets.ShuffleButton
import dev.olog.presentation.widgets.switcher.CustomViewSwitcher
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import dev.olog.shared.widgets.AnimatedImageView
import dev.olog.media.widget.CustomSeekBar
import dev.olog.presentation.widgets.textview.ExplicitView

internal class PlayerFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val musicPrefs: MusicPreferencesGateway,
    private val dragListener: IDragListener,
    private val playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior
) : ObservableAdapter<DisplayableItem>(
    lifecycle,
    DiffCallbackDisplayableItem
), TouchableAdapter {

    private val playerViewTypes = listOf(
        R.layout.player_layout_default,
        R.layout.player_layout_spotify,
        R.layout.player_layout_flat,
        R.layout.player_layout_big_image,
        R.layout.player_layout_fullscreen,
        R.layout.player_layout_clean,
        R.layout.player_layout_mini
    )

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is DisplayableTrack)
                    mediaProvider.skipToQueueItem(item.idInPlaylist)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    navigator.toDialog(item.mediaId, view)
                }
                viewHolder.elevateAlbumOnTouch()

                viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
            }
            R.layout.player_layout_default,
            R.layout.player_layout_spotify,
            R.layout.player_layout_fullscreen,
            R.layout.player_layout_flat,
            R.layout.player_layout_big_image,
            R.layout.player_layout_clean,
            R.layout.player_layout_mini -> {
                setupListeners(viewHolder)

                viewHolder.setOnClickListener(R.id.more, this) { _, _, view ->
                    try {
                        val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                        navigator.toDialog(mediaId, view)
                    } catch (ex: NullPointerException){
                        ex.printStackTrace()
                    }
                }
                viewHolder.itemView.findViewById<dev.olog.presentation.widgets.VolumeChangerView>(R.id.volume)?.musicPrefs = musicPrefs
            }
        }

    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)

        val viewType = holder.itemViewType

        if (viewType in playerViewTypes) {

            val view = holder.itemView
            view.findViewById<CustomViewSwitcher>(R.id.imageSwitcher)?.let {
                it.observeProcessorColors()
                    .asLiveData()
                    .subscribe(holder, viewModel::updateProcessorColors)
                it.observePaletteColors()
                    .asLiveData()
                    .subscribe(holder, viewModel::updatePaletteColors)
            }
            view.findViewById<PlayerImageView>(R.id.miniCover)?.let {
                it.observeProcessorColors()
                    .asLiveData()
                    .subscribe(holder, viewModel::updateProcessorColors)
                it.observePaletteColors()
                    .asLiveData()
                    .subscribe(holder, viewModel::updatePaletteColors)
            }

            bindPlayerControls(holder, view)

            playerAppearanceAdaptiveBehavior(holder, viewModel)
        }
    }

    private fun setupListeners(holder: DataBoundViewHolder) {
        val view = holder.itemView
        view.findViewById<View>(R.id.repeat).setOnClickListener { mediaProvider.toggleRepeatMode() }
        view.findViewById<View>(R.id.shuffle).setOnClickListener { mediaProvider.toggleShuffleMode() }
        val favorite = view.findViewById<LottieFavorite>(R.id.favorite)
        favorite.setOnClickListener {
            favorite.toggleFavorite()
            mediaProvider.togglePlayerFavorite()
        }
        view.findViewById<View>(R.id.lyrics).setOnClickListener { navigator.toOfflineLyrics() }
        view.findViewById<View>(R.id.next).setOnClickListener { mediaProvider.skipToNext() }
        view.findViewById<View>(R.id.playPause).setOnClickListener { mediaProvider.playPause() }
        view.findViewById<View>(R.id.previous).setOnClickListener { mediaProvider.skipToPrevious() }

        view.findViewById<View>(R.id.replay)?.setOnClickListener {
            it.rotate(-30f)
            mediaProvider.replayTenSeconds()
        }

        view.findViewById<View>(R.id.replay30)?.setOnClickListener {
            it.rotate(-50f)
            mediaProvider.replayThirtySeconds()
        }

        view.findViewById<View>(R.id.forward)?.setOnClickListener {
            it.rotate(30f)
            mediaProvider.forwardTenSeconds()
        }

        view.findViewById<View>(R.id.forward30)?.setOnClickListener {
            it.rotate(50f)
            mediaProvider.forwardThirtySeconds()
        }

        view.findViewById<View>(R.id.playbackSpeed).setOnClickListener { openPlaybackSpeedPopup(it) }

        val seekBar = view.findViewById<CustomSeekBar>(R.id.seekBar)
        seekBar.setListener(
            onProgressChanged = {
                view.findViewById<TextView>(R.id.bookmark).text = TextUtils.formatMillis(it)
            }, onStartTouch = {

            }, onStopTouch = {
                mediaProvider.seekTo(it.toLong())
            }
        )
    }

    private fun bindPlayerControls(holder: DataBoundViewHolder, view: View) {
        val playerAppearance = view.context.hasPlayerAppearance()

        if (!playerAppearance.isSpotify() && !playerAppearance.isBigImage()){
            view.findViewById<AnimatedImageView>(R.id.next).setDefaultColor()
            view.findViewById<AnimatedImageView>(R.id.previous).setDefaultColor()
            view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause).setDefaultColor()
        }

        mediaProvider.observeMetadata()
            .subscribe(holder) {
                viewModel.updateCurrentTrackId(it.id)

                updateMetadata(view, it)
                updateImage(view, it)
            }

        view.findViewById<View>(R.id.volume)?.setOnClickListener {
            val outLocation = intArrayOf(0, 0)
            it.getLocationInWindow(outLocation)
            val yLocation = (outLocation[1] - StatusBarView.viewHeight).toFloat()
            view.context.findActivity().fragmentTransaction {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, PlayerVolumeFragment.newInstance(
                    R.layout.player_volume,
                    yLocation
                ), PlayerVolumeFragment.TAG)
                addToBackStack(PlayerVolumeFragment.TAG)
            }
        }

        mediaProvider.observePlaybackState()
            .subscribe(holder) { onPlaybackStateChanged(view, it) }

        mediaProvider.observePlaybackState()
            .subscribe(holder) { view.findViewById<CustomSeekBar>(R.id.seekBar).onStateChanged(it) }

        mediaProvider.observeRepeat()
            .subscribe(holder, view.findViewById<RepeatButton>(R.id.repeat)::cycle)

        mediaProvider.observeShuffle()
            .subscribe(holder, view.findViewById<ShuffleButton>(R.id.shuffle)::cycle)

        view.findViewById<SwipeableView>(R.id.swipeableView)?.setOnSwipeListener(object : SwipeableView.SwipeListener {
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
            .subscribe(holder, view.findViewById<LottieFavorite>(R.id.favorite)::onNextState)

        viewModel.skipToNextVisibility
            .asLiveData()
            .subscribe(holder, view.findViewById<AnimatedImageView>(R.id.next)::updateVisibility)

        viewModel.skipToPreviousVisibility
            .asLiveData()
            .subscribe(holder, view.findViewById<AnimatedImageView>(R.id.previous)::updateVisibility)

        viewModel.observePlayerControlsVisibility()
            .filter { !playerAppearance.isFullscreen()
                    && !playerAppearance.isMini()
                    && !playerAppearance.isSpotify()
                    && !playerAppearance.isBigImage()
            }
            .asLiveData()
            .subscribe(holder) { visible ->
                view.findViewById<View>(R.id.playerControls)
                    ?.findViewById<View>(R.id.player)
                    ?.toggleVisibility(visible, true)
            }


        mediaProvider.observePlaybackState()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .subscribe(holder) {
                animateSkipTo(view, it)
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .subscribe(holder) { state ->
                when (state) {
                    PlayerState.PLAYING -> playAnimation(view)
                    PlayerState.PAUSED -> pauseAnimation(view)
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }
    }

    private fun updateMetadata(view: View, metadata: PlayerMetadata) {
        val title = view.findViewById<TextView>(R.id.title)
        if (view.context.hasPlayerAppearance().isFlat()){
            // WORKAROUND, all caps attribute is not working for some reason
            title.text = metadata.title.uppercase()
        } else {
            title.text = metadata.title
        }
        view.findViewById<TextView>(R.id.artist).text = metadata.artist

        val duration = metadata.duration

        val readableDuration = metadata.readableDuration
        view.findViewById<TextView>(R.id.duration).text = readableDuration
        view.findViewById<CustomSeekBar>(R.id.seekBar).max = duration.toInt()

        val isPodcast = metadata.isPodcast
        val playerControlsRoot = view.findViewById<ViewGroup>(R.id.playerControls)
        playerControlsRoot?.findViewById<View>(R.id.podcast_controls)?.toggleVisibility(isPodcast, true)
    }

    private fun updateImage(view: View, metadata: PlayerMetadata) {
        view.findViewById<CustomViewSwitcher>(R.id.imageSwitcher)?.loadImage(metadata)
        view.findViewById<PlayerImageView>(R.id.miniCover)?.loadImage(metadata.mediaId)
    }

    private fun openPlaybackSpeedPopup(view: View) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.dialog_playback_speed)
        popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
        popup.setOnMenuItemClickListener {
            viewModel.setPlaybackSpeed(it.itemId)
            true
        }
        popup.show()
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlayerPlaybackState) {
        val isPlaying = playbackState.isPlaying

        if (isPlaying || playbackState.isPaused) {
            view.findViewById<View>(R.id.nowPlaying)?.isActivated = isPlaying
            view.findViewById<CustomViewSwitcher>(R.id.imageSwitcher)?.setChildrenActivated(isPlaying)
        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = view.context.asType<HasSlidingPanel>()
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.findViewById<AnimatedImageView>(R.id.next).playAnimation()
        } else {
            view.findViewById<AnimatedImageView>(R.id.previous).playAnimation()
        }
    }

    private fun playAnimation(view: View) {
        val hasSlidingPanel = view.context.asType<HasSlidingPanel>()
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause).animationPlay(isPanelExpanded)
    }

    private fun pauseAnimation(view: View) {
        val hasSlidingPanel = view.context.asType<HasSlidingPanel>()
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause).animationPause(isPanelExpanded)
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        if (item is DisplayableTrack){
            holder.itemView.apply {
                BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
                findViewById<TextView>(R.id.firstText).text = item.title
                findViewById<TextView>(R.id.secondText).text = item.artist
                findViewById<ExplicitView>(R.id.explicit).onItemChanged(item.title)
            }
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_mini_queue
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - 1
        val realTo = to - 1
        mediaProvider.swapRelative(realFrom, realTo)
        dataSet.swap(from, to)
        notifyItemMoved(from, to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.removeRelative(realPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        dataSet.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.moveRelative(realPosition)
        notifyItemChanged(viewHolder.adapterPosition)
    }

}