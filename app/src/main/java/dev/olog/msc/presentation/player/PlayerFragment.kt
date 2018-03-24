package dev.olog.msc.presentation.player

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.player_controls.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerFragment : BaseFragment() {

    @Inject lateinit var viewModel: PlayerFragmentViewModel
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var adapter : PlayerFragmentAdapter
    private lateinit var layoutManager : LinearLayoutManager

    private lateinit var mediaProvider : MediaProvider

    private var seekBarDisposable : Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaProvider = (activity as MediaProvider)

        mediaProvider.onQueueChanged().mapToList { it.toDisplayableItem() }
                .map { queue ->
                    val copy = queue.toMutableList()
                    if (copy.size > PlaylistConstants.MINI_QUEUE_SIZE - 1){
                        copy.add(viewModel.footerLoadMore)
                    }
                    copy.add(0, viewModel.playerControls)
                    copy
                }
                .asLiveData()
                .subscribe(this, viewModel::updateQueue)

        viewModel.observeMiniQueue()
                .subscribe(this, adapter::updateDataSet)

        mediaProvider.onStateChanged()
                .asLiveData()
                .subscribe(this, {
                    val bookmark = it.position.toInt()
                    viewModel.updateProgress(bookmark)
                    handleSeekBar(bookmark, it.state == PlaybackStateCompat.STATE_PLAYING)
                })

        if (act.isLandscape){
            mediaProvider.onMetadataChanged()
                    .asLiveData()
                    .subscribe(this, { PlayerImage.loadImage(cover, it) })

            mediaProvider.onStateChanged()
                    .asLiveData()
                    .subscribe(this, {
                        if (it.isPlaying() || it.isPaused()){
                            cover?.isActivated = it.isPlaying()
                        }
                    })

            mediaProvider.onRepeatModeChanged()
                    .asLiveData()
                    .subscribe(this, repeat::cycle)

            mediaProvider.onShuffleModeChanged()
                    .asLiveData()
                    .subscribe(this, shuffle::cycle)

            mediaProvider.onStateChanged()
                    .map { it.state }
                    .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                            state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
                    .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                    .asLiveData()
                    .subscribe(this, this::animateSkipTo)

            mediaProvider.onStateChanged()
                    .map { it.state }
                    .filter { it == PlaybackStateCompat.STATE_PLAYING ||
                            it == PlaybackStateCompat.STATE_PAUSED
                    }.distinctUntilChanged()
                    .asLiveData()
                    .subscribe(this, { state ->

                        if (state == PlaybackStateCompat.STATE_PLAYING){
                            playAnimation(true)
                        } else {
                            pauseAnimation(true)
                        }
                    })

            RxView.clicks(next)
                    .asLiveData()
                    .subscribe(this, { mediaProvider.skipToNext() })

            RxView.clicks(playPause)
                    .asLiveData()
                    .subscribe(this, { mediaProvider.playPause() })

            RxView.clicks(previous)
                    .asLiveData()
                    .subscribe(this, { mediaProvider.skipToPrevious() })

            viewModel.observePlayerControlsVisibility()
                    .asLiveData()
                    .subscribe(this, {
                        previous.toggleVisibility(it)
                        playPause.toggleVisibility(it)
                        next.toggleVisibility(it)
                    })

            viewModel.skipToNextVisibility.asLiveData()
                    .subscribe(this, next::updateVisibility)

            viewModel.skipToPreviousVisibility.asLiveData()
                    .subscribe(this, previous::updateVisibility)
        }
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(context!!)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.setHasFixedSize(true)
        val callback = TouchHelperAdapterCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isCollapsed()) return

        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun playAnimation(animate: Boolean) {
        playPause.animationPlay(getSlidingPanel().isExpanded() && animate)
    }

    private fun pauseAnimation(animate: Boolean) {
        playPause.animationPause(getSlidingPanel().isExpanded() && animate)
    }

    private fun handleSeekBar(bookmark: Int, isPlaying: Boolean){
        seekBarDisposable.unsubscribe()

        if (isPlaying){
            seekBarDisposable = Observable.interval(PROGRESS_BAR_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
                    .map { (it + 1) * PROGRESS_BAR_INTERVAL + bookmark }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ viewModel.updateProgress(it.toInt()) }, Throwable::printStackTrace)
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.slidingPanel.setScrollableView(list)
        swipeableView?.setOnSwipeListener(onSwipeListener)
        shuffle?.setOnClickListener { mediaProvider.toggleShuffleMode() }
        repeat?.setOnClickListener { mediaProvider.toggleRepeatMode() }
    }

    override fun onPause() {
        super.onPause()
        swipeableView?.setOnSwipeListener(null)
        shuffle?.setOnClickListener(null)
        repeat?.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        seekBarDisposable.unsubscribe()
    }

    private val onSwipeListener = object : SwipeableView.SwipeListener {
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
    }


    private fun MediaSessionCompat.QueueItem.toDisplayableItem(): DisplayableItem {
        val description = this.description

        return DisplayableItem(
                R.layout.item_mini_queue,
                MediaId.fromString(description.mediaId!!),
                description.title!!.toString(),
                DisplayableItem.adjustArtist(description.subtitle!!.toString()),
                description.mediaUri!!.toString(),
                isPlayable = true,
                trackNumber = "${this.queueId}"
        )
    }


    override fun provideLayoutId(): Int = R.layout.fragment_player
}