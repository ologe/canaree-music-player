package dev.olog.msc.presentation.player

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.FragmentLifecycle
import dev.olog.msc.floating.window.service.FloatingWindowHelper
import dev.olog.msc.presentation.SeekBarObservable
import dev.olog.msc.presentation.base.HasSlidingPanel
import dev.olog.msc.presentation.base.adapter.AbsAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.fragment_player_controls.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.view.*
import kotlinx.android.synthetic.main.layout_swipeable_view.view.*
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
        }
    }

    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        val viewType = holder.itemViewType
        when (viewType){
            R.layout.fragment_player_controls -> {
                bindPlayerControls(holder.itemView)
            }
        }
    }

    private fun bindPlayerControls(view: View){
        mediaProvider.onMetadataChanged()
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateMetadata(view, it)
                    updateImage(view, it)
                }, Throwable::printStackTrace)

        mediaProvider.onStateChanged()
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onPlaybackStateChanged(view, it) }, Throwable::printStackTrace)

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

        RxView.clicks(view.floatingWindow)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    FloatingWindowHelper.startServiceOrRequestOverlayPermission(activity)
                }, Throwable::printStackTrace)

        RxView.clicks(view.favorite)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mediaProvider.togglePlayerFavorite() }, Throwable::printStackTrace)

        RxView.clicks(view.playingQueue)
                .takeUntil(RxView.detaches(view))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ navigator.toPlayingQueueFragment(view.playingQueue) }, Throwable::printStackTrace)

        val seekBarObservable = SeekBarObservable(view.seekBar)
                .share()

        seekBarObservable.ofType<Int>()
                .takeUntil(RxView.detaches(view))
                .map { TextUtils.getReadableSongLength(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view.bookmark::setText, Throwable::printStackTrace)

        seekBarObservable.ofType<Pair<SeekBarObservable.Notification, Int>>()
                .takeUntil(RxView.detaches(view))
                .filter { (notification, _) -> notification == SeekBarObservable.Notification.STOP }
                .map { (_, progress) -> progress.toLong() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaProvider::seekTo, Throwable::printStackTrace)

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

        if (activity.isPortrait){
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
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ visible ->
                        view.previous.toggleVisibility(visible)
                        view.playPause.toggleVisibility(visible)
                        view.next.toggleVisibility(visible)

                    }, Throwable::printStackTrace)

            viewModel.observeMiniQueueVisibility()
                    .takeUntil(RxView.detaches(view))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ visible ->
                        val alignment = if (visible) View.TEXT_ALIGNMENT_VIEW_START else View.TEXT_ALIGNMENT_CENTER
                        val padding = if (visible) view.context.dip(16) else 0
                        view.title.setPaddingTop(padding)
                        view.title.textAlignment = alignment
                        view.artist.textAlignment = alignment

                        val params = view.layoutParams
                        params.height = if (!visible) ViewGroup.LayoutParams.MATCH_PARENT
                        else ViewGroup.LayoutParams.WRAP_CONTENT
                        view.layoutParams = params

                        val set = ConstraintSet()
                        set.clone(view as ConstraintLayout)

                        if (visible){
                            set.clear(R.id.title, ConstraintSet.BOTTOM)
                        } else {
                            set.connect(R.id.title, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                        }
                        set.applyTo(view)

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
        view.duration.text = metadata.getDurationReadable()
        view.seekBar.max = duration.toInt()
        view.remix.toggleVisibility(metadata.isRemix())
        view.explicit.toggleVisibility(metadata.isExplicit())
    }

    private fun updateImage(view: View, metadata: MediaMetadataCompat){
        view.cover ?: return

        PlayerImage.loadImage(view.cover, metadata)
    }

    private fun onPlaybackStateChanged(view: View, playbackState: PlaybackStateCompat){
        val isPlaying = playbackState.isPlaying()
        if (isPlaying || playbackState.isPaused()){
            view.nowPlaying.isActivated = isPlaying
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