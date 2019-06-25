package dev.olog.msc.presentation.player

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.core.MediaId
import dev.olog.media.*
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.shared.widgets.AnimatedImageView
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.presentation.widget.animateBackgroundColor
import dev.olog.msc.presentation.widget.animateTextColor
import dev.olog.msc.presentation.widget.audiowave.AudioWaveViewWrapper
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import dev.olog.msc.utils.k.extension.*
import dev.olog.presentation.base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.MusicConstants
import dev.olog.shared.extensions.*
import dev.olog.shared.theme.HasImageShape
import dev.olog.shared.theme.HasPlayerAppearance
import dev.olog.shared.theme.ImageShape
import dev.olog.shared.theme.hasPlayerAppearance
import dev.olog.shared.utils.TextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.player_controls.view.*

class PlayerFragmentAdapter (
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator,
        private val viewModel: PlayerFragmentViewModel,
        private val presenter: PlayerFragmentPresenter

): AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        when (viewType){
            R.layout.item_mini_queue -> {
                viewHolder.setOnClickListener(controller) { item, _, _ ->
                    mediaProvider.skipToQueueItem(item.trackNumber.toLong())
                }
                viewHolder.setOnLongClickListener(controller) { item, _, _ ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, controller) { item, _, view ->
                    navigator.toDialog(item, view)
                }
                viewHolder.elevateSongOnTouch()

                viewHolder.setOnMoveListener(controller, touchHelper!!)
            }
            R.layout.fragment_player_controls,
            R.layout.fragment_player_controls_spotify,
            R.layout.fragment_player_controls_fullscreen,
            R.layout.fragment_player_controls_flat,
            R.layout.fragment_player_controls_big_image,
            R.layout.fragment_player_controls_clean,
            R.layout.fragment_player_controls_mini -> {
                viewHolder.setOnClickListener(R.id.more, controller) { _, _, view ->
                    val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                    navigator.toDialog(mediaId, view)
                }
            }
        }

    }

    @SuppressLint("RxLeakedSubscription")
    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        val viewType = holder.itemViewType

        if (viewType in listOf(R.layout.fragment_player_controls,
                        R.layout.fragment_player_controls_spotify,
                        R.layout.fragment_player_controls_flat,
                        R.layout.fragment_player_controls_big_image,
                        R.layout.fragment_player_controls_fullscreen,
                        R.layout.fragment_player_controls_clean,
                        R.layout.fragment_player_controls_mini)) {

            val view = holder.itemView
            view.bigCover?.observeProcessorColors()
                    ?.takeUntil(RxView.detaches(view))
                    ?.subscribe(viewModel::updateProcessorColors, Throwable::printStackTrace)
            view.bigCover?.observePaletteColors()
                    ?.takeUntil(RxView.detaches(view))
                    ?.subscribe(viewModel::updatePaletteColors, Throwable::printStackTrace)

            bindPlayerControls(holder, view)
        }

        val view = holder.itemView
        val hasImageShape = view.context.applicationContext as HasImageShape
        val imageShape = hasImageShape.getImageShape()
        if (imageShape == ImageShape.RECTANGLE){
            view.coverWrapper?.radius = 0f
        }

        when (viewType){
            R.layout.fragment_player_controls,
            R.layout.fragment_player_controls_spotify,
            R.layout.fragment_player_controls_big_image,
            R.layout.fragment_player_controls_clean -> {

                viewModel.observePaletteColors()
                        .takeUntil(RxView.detaches(view))
                        .map { it.accent }
                        .subscribe({ accent ->
                            view.artist.apply { animateTextColor(accent) }
                            view.shuffle?.updateSelectedColor(accent)
                            view.repeat?.updateSelectedColor(accent)
                            view.seekBar.apply {
                                thumbTintList = ColorStateList.valueOf(accent)
                                progressTintList = ColorStateList.valueOf(accent)
                            }
                        }, Throwable::printStackTrace)

            }
            R.layout.fragment_player_controls_flat -> {
                viewModel.observeProcessorColors()
                        .takeUntil(RxView.detaches(view))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ colors ->
                            view.title.apply {
                                animateTextColor(colors.primaryText)
                                animateBackgroundColor(colors.background)
                            }
                            view.artist.apply {
                                animateTextColor(colors.secondaryText)
                                animateBackgroundColor(colors.background)
                            }
                        }, Throwable::printStackTrace)
                viewModel.observePaletteColors()
                        .takeUntil(RxView.detaches(view))
                        .map { it.accent }
                        .subscribe({ accent ->
                            view.seekBar.apply {
                                thumbTintList = ColorStateList.valueOf(accent)
                                progressTintList = ColorStateList.valueOf(accent)
                            }
                            view.shuffle?.updateSelectedColor(accent)
                            view.repeat?.updateSelectedColor(accent)
                        },Throwable::printStackTrace)
            }
            R.layout.fragment_player_controls_fullscreen -> {
                view.playPause.useLightImage()
                view.next.useLightImage()
                view.previous.useLightImage()

                viewModel.observePaletteColors()
                        .takeUntil(RxView.detaches(view))
                        .map { it.accent }
                        .subscribe({ accent ->
                            view.seekBar.apply {
                                thumbTintList = ColorStateList.valueOf(accent)
                                progressTintList = ColorStateList.valueOf(accent)
                            }
                            view.artist.animateTextColor(accent)
                            view.playPause.backgroundTintList = ColorStateList.valueOf(accent)
                            view.shuffle.updateSelectedColor(accent)
                            view.repeat.updateSelectedColor(accent)
                        }, Throwable::printStackTrace)
            }
            R.layout.fragment_player_controls_mini -> {
                viewModel.observePaletteColors()
                        .takeUntil(RxView.detaches(view))
                        .map { it.accent }
                        .subscribe({ accent ->
                            view.artist.apply { animateTextColor(accent) }
                            view.shuffle.updateSelectedColor(accent)
                            view.repeat.updateSelectedColor(accent)
                            view.seekBar.apply {
                                thumbTintList = ColorStateList.valueOf(accent)
                                progressTintList = ColorStateList.valueOf(accent)
                            }
                            view.more.imageTintList = ColorStateList.valueOf(accent)
                            view.lyrics.imageTintList = ColorStateList.valueOf(accent)
                        }, Throwable::printStackTrace)
            }
        }
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    // using -> takeUntil(RxView.detaches(view))
    private fun bindPlayerControls(holder: DataBoundViewHolder, view: View){

        val waveWrapper : AudioWaveViewWrapper? = view.findViewById(R.id.waveWrapper)

        view.findViewById<AnimatedImageView>(R.id.next)?.setDefaultColor()
        view.findViewById<AnimatedImageView>(R.id.previous)?.setDefaultColor()
        view.findViewById<AnimatedPlayPauseImageView>(R.id.playPause)?.setDefaultColor()

        mediaProvider.observeMetadata()
                .subscribe(holder) {
                    viewModel.updateCurrentTrackId(it.getId())
                    waveWrapper?.onTrackChanged(it.getString(MusicConstants.PATH))
                    waveWrapper?.updateMax(it.getDuration())

                    updateMetadata(view, it)
                    updateImage(view, it)
                }

        mediaProvider.observePlaybackState()
                .subscribe(holder) { onPlaybackStateChanged(view, it) }

        view.seekBar.setListener(
                onProgressChanged = {
                    view.bookmark.text = TextUtils.formatMillis(it)
                }, onStartTouch = {

                }, onStopTouch = {
                    mediaProvider.seekTo(it.toLong())
                })

        viewModel.observeProgress
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.seekBar.setProgress(it)
                    waveWrapper?.updateProgress(it)
                }, Throwable::printStackTrace)

        mediaProvider.observeRepeat()
            .subscribe(holder, view.repeat::cycle)

        RxView.clicks(view.repeat)
            .takeUntil(RxView.detaches(view))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mediaProvider.toggleRepeatMode() }, Throwable::printStackTrace)

        mediaProvider.observeShuffle()
            .subscribe(holder, view.shuffle::cycle)

        RxView.clicks(view.shuffle)
            .takeUntil(RxView.detaches(view))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ mediaProvider.toggleShuffleMode() }, Throwable::printStackTrace)

        RxView.clicks(view.favorite)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mediaProvider.togglePlayerFavorite() }, Throwable::printStackTrace)

        view.swipeableView?.setOnSwipeListener(object : SwipeableView.SwipeListener{
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
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.favorite::onNextState, Throwable::printStackTrace)

        RxView.clicks(view.lyrics)
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    navigator.toOfflineLyrics()
                }, Throwable::printStackTrace)

        val replayView = view.findViewById<View>(R.id.replay)
        RxView.clicks(replayView)
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    replayView.animate().cancel()
                    replayView.animate().rotation(-30f)
                            .setDuration(200)
                            .withEndAction { replayView.animate().rotation(0f).setDuration(200) }
                    mediaProvider.replayTenSeconds()
                }, Throwable::printStackTrace)

        val replay30View = view.findViewById<View>(R.id.replay30)
        RxView.clicks(replay30View)
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    replay30View.animate().cancel()
                    replay30View.animate().rotation(-50f)
                            .setDuration(200)
                            .withEndAction { replay30View.animate().rotation(0f).setDuration(200) }
                    mediaProvider.replayTenSeconds()
                }, Throwable::printStackTrace)

        val forwardView = view.findViewById<View>(R.id.forward)
        RxView.clicks(forwardView)
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    forwardView.animate().cancel()
                    forwardView.animate().rotation(30f)
                            .setDuration(200)
                            .withEndAction { forwardView.animate().rotation(0f).setDuration(200) }
                    mediaProvider.forwardTenSeconds()
                }, Throwable::printStackTrace)

        val forward30View = view.findViewById<View>(R.id.forward30)
        RxView.clicks(forward30View)
                .takeUntil(RxView.detaches(view))
                .subscribe({
                    forward30View.animate().cancel()
                    forward30View.animate().rotation(50f)
                            .setDuration(200)
                            .withEndAction { forward30View.animate().rotation(0f).setDuration(200) }
                    mediaProvider.forwardTenSeconds()
                }, Throwable::printStackTrace)

        val playbackSpeed = view.findViewById<View>(R.id.playbackSpeed)
        RxView.clicks(playbackSpeed)
                .takeUntil(RxView.detaches(playbackSpeed))
                .subscribe({
                    openPlaybackSpeedPopup(playbackSpeed)
                }, Throwable::printStackTrace)

        val playerAppearance = view.context.hasPlayerAppearance()
        if (playerAppearance.isFullscreen() || playerAppearance.isMini()){

            mediaProvider.observePlaybackState()
                    .map { it.state }
                    .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                            state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
                    .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                    .subscribe(holder) { animateSkipTo(view, it) }

            mediaProvider.observePlaybackState()
                    .map { it.state }
                    .filter { it == PlaybackStateCompat.STATE_PLAYING ||
                            it == PlaybackStateCompat.STATE_PAUSED
                    }.distinctUntilChanged()
                    .subscribe(holder) { state ->

                        if (state == PlaybackStateCompat.STATE_PLAYING){
                            playAnimation(view, true)
                        } else {
                            pauseAnimation(view, true)
                        }
                    }

            RxView.clicks(view.next)
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ mediaProvider.skipToNext() }, Throwable::printStackTrace)

            RxView.clicks(view.playPause)
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ mediaProvider.playPause() }, Throwable::printStackTrace)

            RxView.clicks(view.previous)
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ mediaProvider.skipToPrevious() }, Throwable::printStackTrace)

            presenter.observePlayerControlsVisibility()
                    .filter { !playerAppearance.isFullscreen () && !playerAppearance.isMini() }
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ visible ->
                        view.previous.toggleVisibility(visible, true)
                        view.playPause.toggleVisibility(visible, true)
                        view.next.toggleVisibility(visible, true)

                    }, Throwable::printStackTrace)

            viewModel.skipToNextVisibility
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.next::updateVisibility, Throwable::printStackTrace)

            viewModel.skipToPreviousVisibility
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.previous::updateVisibility, Throwable::printStackTrace)
        }
    }

    private fun updateMetadata(view: View, metadata: MediaMetadataCompat){
        view.title.text = metadata.getTitle()
        view.artist.text = metadata.getArtist()

        val duration = metadata.getDuration()

        val readableDuration = metadata.getDurationReadable()
        view.duration.text = readableDuration
        view.seekBar.max = duration.toInt()

        val isPodcast = metadata.isPodcast()
        val playerControlsRoot: ConstraintLayout = view.findViewById(R.id.playerControls)
                ?: view.findViewById(R.id.playerRoot) as ConstraintLayout
        playerControlsRoot.findViewById<View>(R.id.replay).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.replay30).toggleVisibility(isPodcast, true)
        playerControlsRoot.findViewById<View>(R.id.forward30).toggleVisibility(isPodcast, true)
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat){
        view.bigCover?.loadImage(metadata) ?: return
    }

    private fun openPlaybackSpeedPopup(view: View){
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.dialog_playback_speed)
        popup.menu.getItem(viewModel.getPlaybackSpeed()).isChecked = true
        popup.setOnMenuItemClickListener {
            viewModel.setPlaybackSpeed(it.itemId)
            true
        }
        popup.show()
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat){
        val isPlaying = playbackState.isPlaying()
        if (isPlaying || playbackState.isPaused()){
            view.nowPlaying.isActivated = isPlaying
            val playerAppearance = view.context.hasPlayerAppearance()
            if (playerAppearance.isClean()){
                view.bigCover.isActivated = isPlaying
            } else {
                view.coverWrapper.isActivated = isPlaying
            }

        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPlay(isPanelExpanded && animate)
    }

    private fun pauseAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = (view.context) as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPause(isPanelExpanded && animate)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override val onDragAction = { from: Int, to: Int -> mediaProvider.swapRelative(from, to) }

    override val onSwipeRightAction = { position: Int -> mediaProvider.removeRelative(position) }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_mini_queue
    }
}