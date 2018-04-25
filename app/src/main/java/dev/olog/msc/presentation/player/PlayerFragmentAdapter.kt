package dev.olog.msc.presentation.player

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.res.ColorStateList
import android.databinding.ViewDataBinding
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.MotionEvent
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.utils.images.ColorUtil
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.presentation.widget.animateBackgroundColor
import dev.olog.msc.presentation.widget.animateTextColor
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.player_controls.view.*
import javax.inject.Inject

class PlayerFragmentAdapter @Inject constructor(
        private val activity: Activity,
        @FragmentLifecycle lifecycle: Lifecycle,
        private val mediaProvider: MediaProvider,
        private val navigator: Navigator,
        private val viewModel: PlayerFragmentViewModel

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

                viewHolder.itemView.findViewById<View>(R.id.dragHandle)?.setOnTouchListener { _, event ->
                    if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper?.startDrag(viewHolder)
                        true
                    } else false
                }
            }
            R.layout.fragment_player_controls,
            R.layout.fragment_player_controls_spotify,
            R.layout.fragment_player_controls_fullscreen,
            R.layout.fragment_player_controls_flat -> {
                viewHolder.setOnClickListener(R.id.more, controller) { _, _, view ->
                    val mediaId = MediaId.songId(viewModel.getCurrentTrackId())
                    navigator.toDialog(mediaId, view)
                }
            }
        }
        if (viewType == R.layout.fragment_player_controls_fullscreen){
            viewHolder.setOnClickListener(R.id.cover, controller) { _, _, view ->
                view.animate().cancel()
                val layout = viewHolder.itemView.findViewById(R.id.scrim) as IntercettableTouchConstraintLayout
                if (viewModel.updateFullscreen()){
                    layout.animate().alpha(1f).withEndAction { layout.setTouchEnabled(true) }.duration = 300
                } else {
                    layout.animate().alpha(0f).withStartAction { layout.setTouchEnabled(false) }.duration = 300
                }

            }
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        val viewType = holder.itemViewType
        when (viewType){
            R.layout.fragment_player_controls,
            R.layout.fragment_player_controls_spotify,
            R.layout.fragment_player_controls_fullscreen -> {
                bindPlayerControls(holder.itemView)
            }
            R.layout.fragment_player_controls_flat -> {
                val view = holder.itemView
                bindPlayerControls(view)
                viewModel.observeImageColors()
                        .subscribe({
                            view.title.apply {
                                animateTextColor(it.primaryTextColor)
                                animateBackgroundColor(it.background)
                            }
                            view.artist.apply {
                                animateTextColor(it.secondaryTextColor)
                                animateBackgroundColor(it.background)
                            }
                            view.seekBar.apply {
                                thumbTintList = ColorStateList.valueOf(it.background)
                                progressTintList = ColorStateList.valueOf(it.background)
                                progressBackgroundTintList = ColorStateList.valueOf(ColorUtil.darker(it.background, .5f))
                            }
                        }, Throwable::printStackTrace)
            }
        }
    }

    @SuppressLint("RxLeakedSubscription")
    // using -> takeUntil(RxView.detaches(view))
    private fun bindPlayerControls(view: View){
        mediaProvider.onMetadataChanged()
                .takeUntil(RxView.detaches(view))
                .doOnNext { viewModel.updateCurrentTrackId(it.getId()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewModel.onMetadataChanged(activity, it)
                    updateMetadata(view, it)
                    updateImage(view, it)
                }, Throwable::printStackTrace)

        mediaProvider.onStateChanged()
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onPlaybackStateChanged(view, it) }, Throwable::printStackTrace)

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
                .subscribe(view.seekBar::setProgress, Throwable::printStackTrace)

        if (view.repeat != null){
            mediaProvider.onRepeatModeChanged()
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.repeat::cycle, Throwable::printStackTrace)

            RxView.clicks(view.repeat)
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ mediaProvider.toggleRepeatMode() }, Throwable::printStackTrace)
        }
        if (view.shuffle != null){
            mediaProvider.onShuffleModeChanged()
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view.shuffle::cycle, Throwable::printStackTrace)


            RxView.clicks(view.shuffle)
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ mediaProvider.toggleShuffleMode() }, Throwable::printStackTrace)
        }

        RxView.clicks(view.favorite)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mediaProvider.togglePlayerFavorite() }, Throwable::printStackTrace)

        RxView.clicks(view.playingQueue)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ navigator.toPlayingQueueFragment(view.playingQueue) }, Throwable::printStackTrace)

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

        if (!AppConstants.THEME.isFullscreen()){
            RxView.clicks(view.lyrics)
                    .takeUntil(RxView.detaches(view))
                    .subscribe({
                        navigator.toOfflineLyrics(view.lyrics)
                    }, Throwable::printStackTrace)
        }

        if (activity.isPortrait || AppConstants.THEME.isFullscreen()){
            mediaProvider.onStateChanged()
                    .takeUntil(RxView.detaches(view))
                    .map { it.state }
                    .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                            state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
                    .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ animateSkipTo(view, it) }, Throwable::printStackTrace)

            mediaProvider.onStateChanged()
                    .takeUntil(RxView.detaches(view))
                    .map { it.state }
                    .filter { it == PlaybackStateCompat.STATE_PLAYING ||
                            it == PlaybackStateCompat.STATE_PAUSED
                    }.distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ state ->

                        if (state == PlaybackStateCompat.STATE_PLAYING){
                            playAnimation(view, true)
                        } else {
                            pauseAnimation(view, true)
                        }
                    }, Throwable::printStackTrace)

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

            viewModel.observePlayerControlsVisibility()
                    .filter { !AppConstants.THEME.isFullscreen() }
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
        val readableDuration = if (!AppConstants.THEME.isDefault()){
            metadata.getDurationReadable()
        } else "${TextUtils.MIDDLE_DOT_SPACED}${metadata.getDurationReadable()}"
        view.duration.text = readableDuration
        view.seekBar.max = duration.toInt()
        view.remix?.toggleVisibility(metadata.isRemix(), true)
        view.explicit?.toggleVisibility(metadata.isExplicit(), true)
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat){
        view.cover?.loadImage(metadata) ?: return
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat){
        val isPlaying = playbackState.isPlaying()
        if (isPlaying || playbackState.isPaused()){
            view.nowPlaying?.isActivated = isPlaying
            view.cover?.isActivated = isPlaying
        }
    }

    private fun animateSkipTo(view: View, toNext: Boolean) {
        val hasSlidingPanel = activity as HasSlidingPanel
        if (hasSlidingPanel.getSlidingPanel().isCollapsed()) return

        if (toNext) {
            view.next.playAnimation()
        } else {
            view.previous.playAnimation()
        }
    }

    private fun playAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = activity as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPlay(isPanelExpanded && animate)
    }

    private fun pauseAnimation(view: View, animate: Boolean) {
        val hasSlidingPanel = activity as HasSlidingPanel
        val isPanelExpanded = hasSlidingPanel.getSlidingPanel().isExpanded()
        view.playPause.animationPause(isPanelExpanded && animate)
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }

    override val onDragAction = { from: Int, to: Int -> mediaProvider.swapRelative(from, to) }

    override val onSwipeAction = { position: Int -> mediaProvider.removeRelative(position) }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.item_mini_queue
    }
}