package dev.olog.presentation.player

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.lib.media.MediaProvider
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.lib.media.model.PlayerPlaybackState
import dev.olog.lib.media.model.PlayerState
import dev.olog.lib.image.provider.ImageLoader
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.NavigatorLegacy
import dev.olog.presentation.player.volume.PlayerVolumeFragment
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.widgets.StatusBarView
import dev.olog.shared.widgets.swipeable.SwipeableView
import dev.olog.shared.TextUtils
import dev.olog.shared.android.extensions.findActivity
import dev.olog.shared.android.slidingPanel
import dev.olog.shared.android.theme.playerAppearanceAmbient
import dev.olog.shared.swapped
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
    private val navigator: NavigatorLegacy,
    private val viewModel: PlayerFragmentViewModel,
    private val presenter: PlayerFragmentPresenter,
    private val dragListener: IDragListener,
    private val playerAppearanceAdaptiveBehavior: IPlayerAppearanceAdaptiveBehavior
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem),
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

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
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
                viewHolder.setupListeners()

                viewHolder.setOnClickListener(R.id.more, this) { _, _, view ->
                    try {
                        val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                        navigator.toDialog(mediaId, view)
                    } catch (ex: NullPointerException){
                        ex.printStackTrace()
                    }
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
                    .onEach(presenter::updateProcessorColors)
                    .launchIn(holder.coroutineScope)
                it.observePaletteColors()
                    .onEach(presenter::updatePaletteColors)
                    .launchIn(holder.coroutineScope)
            }
            holder.miniCover?.let {
                it.observeProcessorColors()
                    .onEach(presenter::updateProcessorColors)
                    .launchIn(holder.coroutineScope)
                it.observePaletteColors()
                    .onEach(presenter::updatePaletteColors)
                    .launchIn(holder.coroutineScope)
            }

            holder.bindPlayerControls()

            playerAppearanceAdaptiveBehavior(holder, presenter)
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
                bookmark.text = TextUtils.formatMillis(it)
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
                viewModel.updateCurrentTrackId(it.id)
                updateMetadata(it)
                updateImage(it)
            }.launchIn(coroutineScope)

        volume?.setOnClickListener {
            val outLocation = intArrayOf(0, 0)
            it.getLocationInWindow(outLocation)
            val yLocation = (outLocation[1] - StatusBarView.viewHeight).toFloat()
            itemView.findActivity().supportFragmentManager.commit { // TODO move to a navigator
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                add(android.R.id.content, PlayerVolumeFragment.newInstance(
                    R.layout.player_volume,
                    yLocation
                ), PlayerVolumeFragment.TAG)
                addToBackStack(PlayerVolumeFragment.TAG)
            }
        }

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

        presenter.observePlayerControlsVisibility()
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
        seekBar.max = metadata.duration.toInt()

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
        item: DisplayableItem,
        position: Int
    ) = holder.bindView {
        if (item is DisplayableTrack){
            ImageLoader.loadSongImage(imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.artist
            explicit.onItemChanged(item.title)
        }
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