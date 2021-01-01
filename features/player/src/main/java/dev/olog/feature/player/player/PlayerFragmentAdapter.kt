package dev.olog.feature.player.player

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.drag.IDragListener
import dev.olog.feature.base.adapter.drag.TouchableAdapter
import dev.olog.feature.player.R
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.lib.media.model.PlayerPlaybackState
import dev.olog.lib.media.model.PlayerState
import dev.olog.navigation.Navigator
import dev.olog.shared.android.TextUtils
import dev.olog.shared.android.isCollapsed
import dev.olog.shared.android.isExpanded
import dev.olog.shared.android.slidingPanel
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.exhaustive
import dev.olog.shared.swapped
import dev.olog.shared.widgets.swipeable.SwipeableView
import kotlinx.android.synthetic.main.item_mini_queue.*
import kotlinx.android.synthetic.main.layout_view_switcher.*
import kotlinx.android.synthetic.main.player_controls_default.*
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_layout_default.*
import kotlinx.android.synthetic.main.player_layout_default.artist
import kotlinx.android.synthetic.main.player_layout_default.bookmark
import kotlinx.android.synthetic.main.player_layout_default.duration
import kotlinx.android.synthetic.main.player_layout_default.playerControls
import kotlinx.android.synthetic.main.player_layout_default.seekBar
import kotlinx.android.synthetic.main.player_layout_default.title
import kotlinx.android.synthetic.main.player_layout_mini.*
import kotlinx.android.synthetic.main.player_toolbar_default.*
import kotlinx.android.synthetic.main.player_toolbar_default.favorite
import kotlinx.android.synthetic.main.player_toolbar_default.lyrics
import kotlinx.android.synthetic.main.player_toolbar_default.playbackSpeed
import kotlinx.android.synthetic.main.player_toolbar_default.volume
import kotlinx.coroutines.flow.*
import me.saket.cascade.CascadePopupMenu

internal class PlayerFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val dragListener: IDragListener,
    private val playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior,
    private val toPlayerVolume: (View) -> Unit
) : ObservableAdapter<PlayerFragmentModel>(PlayerFragmentModelDiff),
    TouchableAdapter {

    private val playerViewTypes = listOf(
        R.layout.player_layout_default,
        R.layout.player_layout_spotify,
        R.layout.player_layout_flat,
        R.layout.player_layout_big_image,
        R.layout.player_layout_fullscreen,
        R.layout.player_layout_clean,
        R.layout.player_layout_mini
    )

    override fun getItemViewType(position: Int): Int = getItem(position).layoutType

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        when (viewType) {
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(this) { item, _, _ ->
                    require(item is PlayerFragmentModel.MiniQueueItem)
                    mediaProvider.skipToQueueItem(item.serviceProgressive)
                }
                viewHolder.setOnLongClickListener(this) { item, _, _ ->
                    require(item is PlayerFragmentModel.MiniQueueItem)
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
                    require(item is PlayerFragmentModel.MiniQueueItem)
                    navigator.toDialog(item.mediaId, view)
                }
                viewHolder.elevateSongOnTouch()

                viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
            }
            R.layout.player_layout_default,
            R.layout.player_layout_spotify,
            R.layout.player_layout_fullscreen,
            R.layout.player_layout_flat,
            R.layout.player_layout_big_image,
            R.layout.player_layout_clean,
            R.layout.player_layout_mini -> {
                viewHolder.setupListeners()

                viewHolder.setOnClickListener(R.id.more, this) { _, _, view ->
                    val id = viewModel.currentTrackId ?: return@setOnClickListener
                    val mediaId = MediaId.songId(id)
                    navigator.toDialog(mediaId, view)
                }
            }
        }

    }

    override fun onViewAttachedToWindow(holder: LayoutContainerViewHolder) {
        super.onViewAttachedToWindow(holder)

        val viewType = holder.itemViewType

        if (viewType in playerViewTypes) {

            holder.imageSwitcher?.let {
                it.observeProcessorColors()
                    .onEach(viewModel::updateProcessorColors)
                    .launchIn(holder.coroutineScope)
                it.observePaletteColors()
                    .onEach(viewModel::updatePaletteColors)
                    .launchIn(holder.coroutineScope)
            }
            holder.miniCover?.let {
                it.observeProcessorColors()
                    .onEach(viewModel::updateProcessorColors)
                    .launchIn(holder.coroutineScope)
                it.observePaletteColors()
                    .onEach(viewModel::updatePaletteColors)
                    .launchIn(holder.coroutineScope)
            }

            holder.bindPlayerControls()

            playerAppearanceAdaptiveBehavior(holder, viewModel)
        }
    }

    private fun LayoutContainerViewHolder.setupListeners() {
        repeat.setOnClickListener { mediaProvider.toggleRepeatMode() }
        shuffle.setOnClickListener { mediaProvider.toggleShuffleMode() }
        favorite.setOnClickListener {
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

        playbackSpeed.setOnClickListener { openPlaybackSpeedPopup(it) }

        seekBar.setListener(
            onProgressChanged = {
                bookmark.text = TextUtils.formatTimeMillis(it.toLong())
            }, onStartTouch = {

            }, onStopTouch = {
                mediaProvider.seekTo(it.toLong())
            }
        )
    }

    private fun LayoutContainerViewHolder.bindPlayerControls() {
        val playerAppearanceAmbient = context.playerAppearanceAmbient

        if (!playerAppearanceAmbient.isSpotify() && !playerAppearanceAmbient.isBigImage()){
            next.setDefaultColor()
            previous.setDefaultColor()
            playPause.setDefaultColor()
        }

        mediaProvider.metadata
            .onEach {
                viewModel.currentTrackId = it.id
                updateMetadata(it)
                updateImage(it)
            }.launchIn(coroutineScope)

        volume?.setOnClickListener(toPlayerVolume)

        mediaProvider.playbackState
            .onEach {
                onPlaybackStateChanged(it)
                seekBar.onStateChanged(it)
            }
            .launchIn(coroutineScope)

        mediaProvider.repeat
            .onEach(repeat::cycle)
            .launchIn(coroutineScope)

        mediaProvider.shuffle
            .onEach(shuffle::cycle)
            .launchIn(coroutineScope)

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
            .onEach(favorite::onNextState)
            .launchIn(coroutineScope)

        viewModel.skipToNextVisibility
            .onEach(next::updateVisibility)
            .launchIn(coroutineScope)

        viewModel.skipToPreviousVisibility
            .onEach(previous::updateVisibility)
            .launchIn(coroutineScope)

        viewModel.observePlayerControlsVisibility()
            .filter { !playerAppearanceAmbient.isFullscreen()
                    && !playerAppearanceAmbient.isMini()
                    && !playerAppearanceAmbient.isSpotify()
                    && !playerAppearanceAmbient.isBigImage()
            }
            .onEach { visible ->
                playerControls?.player?.isVisible = visible
            }.launchIn(coroutineScope)


        mediaProvider.playbackState
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .onEach { animateSkipTo(it) }
            .launchIn(coroutineScope)

        mediaProvider.playbackState
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> error("invalid state $state")
                }
            }.launchIn(coroutineScope)
    }

    private fun LayoutContainerViewHolder.updateMetadata(metadata: PlayerMetadata) {

        if (context.playerAppearanceAmbient.isFlat()){
            // WORKAROUND, all caps attribute is not working for some reason
            title.text = metadata.title.toUpperCase()
        } else {
            title.text = metadata.title
        }
        artist.text = metadata.artist

        val readableDuration = metadata.readableDuration
        duration.text = readableDuration
        seekBar.max = metadata.duration.toLongMilliseconds().toInt()

        playerControls.podcast_controls.isVisible = metadata.isPodcast
    }

    private fun LayoutContainerViewHolder.updateImage(metadata: PlayerMetadata) {
        imageSwitcher?.loadImage(metadata)
        miniCover?.loadImage(metadata.mediaId)
    }

    private fun openPlaybackSpeedPopup(view: View) {
        val popup = CascadePopupMenu(view.context, view)
        popup.inflate(R.menu.dialog_playback_speed)
        popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
        popup.setOnMenuItemClickListener {
            viewModel.setPlaybackSpeed(it.itemId)
            true
        }
        popup.show()
    }

    private fun LayoutContainerViewHolder.onPlaybackStateChanged(playbackState: PlayerPlaybackState) {
        val isPlaying = playbackState.isPlaying

        if (isPlaying || playbackState.isPaused) {
            nowPlaying?.isActivated = isPlaying
            imageSwitcher?.setChildrenActivated(isPlaying)
        }
    }

    private fun LayoutContainerViewHolder.animateSkipTo(toNext: Boolean) {
        if (itemView.slidingPanel.isCollapsed()) {
            return
        }

        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun LayoutContainerViewHolder.playAnimation() {
        val isPanelExpanded = itemView.slidingPanel.isExpanded()
        playPause.animationPlay(isPanelExpanded)
    }

    private fun LayoutContainerViewHolder.pauseAnimation() {
        val isPanelExpanded = itemView.slidingPanel.isExpanded()
        playPause.animationPause(isPanelExpanded)
    }

    override fun bind(
        holder: LayoutContainerViewHolder,
        item: PlayerFragmentModel,
        position: Int
    ) = holder.bindView {
        when (item) {
            is PlayerFragmentModel.MiniQueueItem -> bindMiniQueueItem(item)
            is PlayerFragmentModel.Content,
            is PlayerFragmentModel.LoadMoreFooter -> {}
        }.exhaustive
    }

    private fun LayoutContainerViewHolder.bindMiniQueueItem(
        item: PlayerFragmentModel.MiniQueueItem
    ) {
        ImageLoader.loadSongImage(cover, item.mediaId)
        firstText.text = item.title
        secondText.text = item.subtitle
        explicit.onItemChanged(item.title)
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_mini_queue
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - 1
        val realTo = to - 1
        mediaProvider.swapRelative(realFrom, realTo)

        submitList(currentList.swapped(from, to))
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.removeRelative(realPosition)
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {
        val newList = currentList.toMutableList()
        newList.removeAt(viewHolder.adapterPosition)
        submitList(newList)
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val realPosition = viewHolder.adapterPosition - 1
        mediaProvider.moveRelative(realPosition)
        notifyItemChanged(viewHolder.adapterPosition)
    }

}

private object PlayerFragmentModelDiff : DiffUtil.ItemCallback<PlayerFragmentModel>() {

    override fun areItemsTheSame(
        oldItem: PlayerFragmentModel,
        newItem: PlayerFragmentModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: PlayerFragmentModel,
        newItem: PlayerFragmentModel
    ): Boolean {
        return oldItem == newItem
    }
}