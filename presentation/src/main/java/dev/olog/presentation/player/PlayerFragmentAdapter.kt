package dev.olog.presentation.player

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerState
import dev.olog.platform.extension.findActivity
import dev.olog.platform.extension.fragmentTransaction
import dev.olog.platform.extension.toggleVisibility
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.*
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.player.volume.PlayerVolumeFragment
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.presentation.widgets.StatusBarView
import dev.olog.presentation.widgets.imageview.PlayerImageView
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import dev.olog.feature.media.api.DurationUtils
import dev.olog.feature.media.api.MediaProvider
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.shared.asLiveData
import dev.olog.shared.distinctUntilChanged
import dev.olog.shared.filter
import dev.olog.shared.map
import dev.olog.shared.subscribe
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.item_mini_queue.view.*
import kotlinx.android.synthetic.main.layout_view_switcher.view.*
import kotlinx.android.synthetic.main.player_controls_default.view.*
import kotlinx.android.synthetic.main.player_controls_default.view.repeat
import kotlinx.android.synthetic.main.player_controls_default.view.shuffle
import kotlinx.android.synthetic.main.player_layout_default.view.artist
import kotlinx.android.synthetic.main.player_layout_default.view.bookmark
import kotlinx.android.synthetic.main.player_layout_default.view.duration
import kotlinx.android.synthetic.main.player_layout_default.view.seekBar
import kotlinx.android.synthetic.main.player_layout_default.view.swipeableView
import kotlinx.android.synthetic.main.player_layout_default.view.title
import kotlinx.android.synthetic.main.player_toolbar_default.view.*
import kotlinx.android.synthetic.main.player_toolbar_default.view.favorite
import kotlinx.android.synthetic.main.player_toolbar_default.view.lyrics
import kotlinx.android.synthetic.main.player_toolbar_default.view.playbackSpeed
import kotlinx.coroutines.flow.filter

internal class PlayerFragmentAdapter(
    lifecycle: Lifecycle,
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: PlayerFragmentViewModel,
    private val presenter: PlayerFragmentPresenter,
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
                        val mediaId = MediaId.songId(viewModel.getCurrentTrackId()!!)
                        navigator.toDialog(mediaId, view)
                    } catch (ex: NullPointerException){
                        ex.printStackTrace()
                    }
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
                    .asLiveData()
                    .subscribe(holder, presenter::updateProcessorColors)
                it.observePaletteColors()
                    .asLiveData()
                    .subscribe(holder, presenter::updatePaletteColors)
            }
            view.findViewById<PlayerImageView>(R.id.miniCover)?.let {
                it.observeProcessorColors()
                    .asLiveData()
                    .subscribe(holder, presenter::updateProcessorColors)
                it.observePaletteColors()
                    .asLiveData()
                    .subscribe(holder, presenter::updatePaletteColors)
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
        view.lyrics.setOnClickListener { navigator.toOfflineLyrics() }
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
                view.bookmark.text = DurationUtils.formatMillis(it)
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
            .subscribe(holder) {
                viewModel.updateCurrentTrackId(it.id)

                updateMetadata(view, it)
                updateImage(view, it)
            }

        view.volume?.setOnClickListener {
            val outLocation = intArrayOf(0, 0)
            it.getLocationInWindow(outLocation)
            val yLocation = (outLocation[1] - StatusBarView.viewHeight).toFloat()
            (view.findActivity()).fragmentTransaction {
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
            .subscribe(holder) { view.seekBar.onStateChanged(it) }

        mediaProvider.observeRepeat()
            .subscribe(holder, view.repeat::cycle)

        mediaProvider.observeShuffle()
            .subscribe(holder, view.shuffle::cycle)

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
            .subscribe(holder, view.favorite::onNextState)

        viewModel.skipToNextVisibility
            .asLiveData()
            .subscribe(holder, view.next::updateVisibility)

        viewModel.skipToPreviousVisibility
            .asLiveData()
            .subscribe(holder, view.previous::updateVisibility)

        presenter.observePlayerControlsVisibility()
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
        playerControlsRoot.podcast_controls.toggleVisibility(isPodcast, true)
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
        if (view.context.slidingPanel.isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View) {
        val isPanelExpanded = view.context.slidingPanel.isExpanded()
        view.playPause.animationPlay(isPanelExpanded)
    }

    private fun pauseAnimation(view: View) {
        val isPanelExpanded = view.context.slidingPanel.isExpanded()
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