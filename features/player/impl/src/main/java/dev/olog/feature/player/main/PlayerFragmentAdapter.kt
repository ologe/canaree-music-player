package dev.olog.feature.player.main

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.media.api.MediaProvider
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerState
import dev.olog.feature.player.R
import dev.olog.feature.player.volume.PlayerVolumeFragment
import dev.olog.feature.player.widget.PlayerImageView
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.HasSlidingPanel
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.shared.TextUtils
import dev.olog.shared.extension.collectOnLifecycle
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.subscribe
import dev.olog.ui.StatusBarView
import dev.olog.ui.extension.isCollapsed
import dev.olog.ui.extension.isExpanded
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import dev.olog.ui.swipeable.SwipeableView
import kotlinx.android.synthetic.main.item_mini_queue.view.*
import kotlinx.android.synthetic.main.layout_view_switcher.view.*
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

internal class PlayerFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val onLyricsButtonClick: () -> Unit,
    private val viewModel: PlayerFragmentViewModel,
    private val presenter: PlayerFragmentPresenter,
    private val musicPrefs: MusicPreferencesGateway,
    private val dragListener: IDragListener,
    private val playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,
    private val onItemLongClick: (View, MediaId) -> Unit,
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

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
                    onItemLongClick(viewHolder.itemView, item.mediaId)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    onItemLongClick(view, item.mediaId)
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
                    val id = viewModel.getCurrentTrackId() ?: return@setOnClickListener
                    val mediaId = MediaId.songId(id)
                    onItemLongClick(view, mediaId)
                }
                viewHolder.itemView.volume?.musicPrefs = musicPrefs
            }
        }

    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)

        val viewType = holder.itemViewType

        if (viewType in playerViewTypes) {

            val view = holder.itemView
            view.imageSwitcher?.let {
                it.observeProcessorColors()
                    .collectOnLifecycle(holder) {
                        presenter.updateProcessorColors(it)
                    }
                it.observePaletteColors()
                    .collectOnLifecycle(holder) {
                        presenter.updatePaletteColors(it)
                    }
            }
            view.findViewById<PlayerImageView>(R.id.miniCover)?.let {
                it.observeProcessorColors()
                    .collectOnLifecycle(holder) {
                        presenter.updateProcessorColors(it)
                    }
                it.observePaletteColors()
                    .collectOnLifecycle(holder) {
                        presenter.updatePaletteColors(it)
                    }
            }

            bindPlayerControls(holder, view)

            playerAppearanceAdaptiveBehavior(holder, presenter)
        }
    }

    private fun setupListeners(holder: DataBoundViewHolder) {
        val view = holder.itemView
        view.repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
        view.shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
        view.favorite.setOnClickListener {
            view.favorite.toggleFavorite()
            mediaProvider.togglePlayerFavorite()
        }
        view.lyrics.setOnClickListener { onLyricsButtonClick() }
        view.next.setOnClickListener { mediaProvider.skipToNext() }
        view.playPause.setOnClickListener { mediaProvider.playPause() }
        view.previous.setOnClickListener { mediaProvider.skipToPrevious() }

        view.replay.setOnClickListener {
            it.rotate(-30f)
            mediaProvider.replayTenSeconds()
        }

        view.replay30.setOnClickListener {
            it.rotate(-50f)
            mediaProvider.replayThirtySeconds()
        }

        view.forward.setOnClickListener {
            it.rotate(30f)
            mediaProvider.forwardTenSeconds()
        }

        view.forward30.setOnClickListener {
            it.rotate(50f)
            mediaProvider.forwardThirtySeconds()
        }

        view.playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(it) }

        view.seekBar.setListener(
            onProgressChanged = {
                view.bookmark.text = TextUtils.formatMillis(it)
            }, onStartTouch = {

            }, onStopTouch = {
                mediaProvider.seekTo(it.toLong())
            }
        )
    }

    private fun bindPlayerControls(holder: DataBoundViewHolder, view: View) {
        val playerAppearance = view.context.hasPlayerAppearance()

        if (!playerAppearance.isSpotify() && !playerAppearance.isBigImage()){
            view.next.setDefaultColor()
            view.previous.setDefaultColor()
            view.playPause.setDefaultColor()
        }

        mediaProvider.observeMetadata()
            .collectOnLifecycle(holder) {
                viewModel.updateCurrentTrackId(it.id)

                updateMetadata(view, it)
                updateImage(view, it)
            }

        view.volume?.setOnClickListener {
            val outLocation = intArrayOf(0, 0)
            it.getLocationInWindow(outLocation)
            val yLocation = (outLocation[1] - StatusBarView.viewHeight).toFloat()
            (view.context.findInContext<FragmentActivity>()).supportFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, PlayerVolumeFragment.newInstance(
                    R.layout.player_volume,
                    yLocation
                ), PlayerVolumeFragment.TAG)
                addToBackStack(PlayerVolumeFragment.TAG)
            }
        }

        mediaProvider.observePlaybackState()
            .collectOnLifecycle(holder) { onPlaybackStateChanged(view, it) }

        mediaProvider.observePlaybackState()
            .collectOnLifecycle(holder) { view.seekBar.onStateChanged(it) }

        mediaProvider.observeRepeat()
            .collectOnLifecycle(holder) {
                view.repeat.cycle(it)
            }

        mediaProvider.observeShuffle()
            .collectOnLifecycle(holder) {
                view.shuffle.cycle(it)
            }

        view.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener {
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
                view.favorite.onNextState(it)
            }

        viewModel.skipToNextVisibility
            .collectOnLifecycle(holder) {
                view.next.updateVisibility(it)
            }

        viewModel.skipToPreviousVisibility
            .collectOnLifecycle(holder) {
                view.previous.updateVisibility(it)
            }

        presenter.observePlayerControlsVisibility()
            .filter { !playerAppearance.isFullscreen()
                    && !playerAppearance.isMini()
                    && !playerAppearance.isSpotify()
                    && !playerAppearance.isBigImage()
            }
            .collectOnLifecycle(holder) { visible ->
                view.findViewById<View>(R.id.playerControls)
                    ?.findViewById<View>(R.id.player)
                    ?.isVisible = visible
            }


        mediaProvider.observePlaybackState()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .collectOnLifecycle(holder) {
                animateSkipTo(view, it)
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .collectOnLifecycle(holder) { state ->
                when (state) {
                    PlayerState.PLAYING -> playAnimation(view)
                    PlayerState.PAUSED -> pauseAnimation(view)
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }
    }

    private fun updateMetadata(view: View, metadata: PlayerMetadata) {
        if (view.context.hasPlayerAppearance().isFlat()){
            // WORKAROUND, all caps attribute is not working for some reason
            view.title.text = metadata.title.toUpperCase()
        } else {
            view.title.text = metadata.title
        }
        view.artist.text = metadata.artist

        val duration = metadata.duration

        val readableDuration = metadata.readableDuration
        view.duration.text = readableDuration
        view.seekBar.max = duration.toInt()

        val isPodcast = metadata.isPodcast
        val playerControlsRoot = view.findViewById<ViewGroup>(R.id.playerControls)
        playerControlsRoot.podcast_controls.isVisible = isPodcast
    }

    private fun updateImage(view: View, metadata: PlayerMetadata) {
        view.imageSwitcher?.loadImage(metadata)
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
            view.nowPlaying?.isActivated = isPlaying
            view.imageSwitcher?.setChildrenActivated(isPlaying)
        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = view.context.findInContext<HasSlidingPanel>()
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View) {
        val hasSlidingPanel = view.context.findInContext<HasSlidingPanel>()
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPlay(isPanelExpanded)
    }

    private fun pauseAnimation(view: View) {
        val hasSlidingPanel = view.context.findInContext<HasSlidingPanel>()
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPause(isPanelExpanded)
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        if (item is DisplayableTrack){
            holder.itemView.apply {
                BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
                firstText.text = item.title
                secondText.text = item.artist
                explicit.onItemChanged(item.title)
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
        swap(from, to)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.removeRelative(realPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        removeAt(viewHolder.adapterPosition)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.moveRelative(realPosition)
        notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
    }
}