package dev.olog.msc.presentation.player

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.constants.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.adapter.TouchHelperAdapterCallback
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.layout_swipeable_view.*
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

        mediaProvider.onQueueChanged()
                .mapToList { it.toDisplayableItem() }
                .asLiveData()
                .subscribe(this, {
                    val queue = it.toMutableList()
                    if (queue.size > PlaylistConstants.MINI_QUEUE_SIZE - 1){
                        queue.add(viewModel.footerLoadMore)
                    }
                    queue.add(0, viewModel.playerControls)
                    adapter.updateDataSet(queue)
                })

        mediaProvider.onStateChanged()
                .filter { act.isLandscape }
                .asLiveData()
                .subscribe(this, {
                    val state = it.state
                    if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED){
                        val isPlaying = state == PlaybackStateCompat.STATE_PLAYING
                        cover?.isActivated = isPlaying
                    }
                })

        mediaProvider.onStateChanged()
                .asLiveData()
                .subscribe(this, {
                    val bookmark = it.position.toInt()
                    viewModel.updateProgress(bookmark)
                    handleSeekBar(bookmark, it.state == PlaybackStateCompat.STATE_PLAYING)
                })

        mediaProvider.onMetadataChanged()
                .filter { act.isLandscape }
                .asLiveData()
                .subscribe(this, {

                    val image = Uri.parse(it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI))
                    val id = MediaId.fromString(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                    ).leaf!!.toInt()
                    val placeholder = CoverUtils.getGradient(ctx, id)

                    GlideApp.with(ctx).clear(cover)

                    GlideApp.with(ctx)
                            .load(image)
                            .centerCrop()
                            .placeholder(placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.IMMEDIATE)
                            .override(800)
                            .into(cover)
                })

        mediaProvider.onRepeatModeChanged()
                .filter { act.isLandscape }
                .asLiveData()
                .subscribe(this, { repeat.cycle(it) })

        mediaProvider.onShuffleModeChanged()
                .filter { act.isLandscape }
                .asLiveData()
                .subscribe(this, { shuffle.cycle(it) })
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

    private val onSwipeListener = object : SwipeableView.SwipeListener{
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
                description.subtitle!!.toString(),
                description.mediaUri!!.toString(),
                isPlayable = true,
                trackNumber = "${this.queueId}"
        )
    }


    override fun provideLayoutId(): Int = R.layout.fragment_player
}