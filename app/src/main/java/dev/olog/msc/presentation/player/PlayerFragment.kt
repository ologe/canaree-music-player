package dev.olog.msc.presentation.player

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.core.MediaId
import dev.olog.core.PlaylistConstants
import dev.olog.media.MediaProvider
import dev.olog.media.extractBookmark
import dev.olog.media.isPlaying
import dev.olog.msc.R
import dev.olog.msc.presentation.base.adapter.drag.TouchHelperAdapterCallback
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.presentation.tutorial.TutorialTapTarget
import dev.olog.msc.presentation.widget.SwipeableView
import dev.olog.msc.utils.k.extension.isCollapsed
import dev.olog.presentation.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.extensions.distinctUntilChanged
import dev.olog.shared.extensions.map
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.isMarshmallow
import dev.olog.shared.lazyFast
import dev.olog.shared.unsubscribe
import dev.olog.shared.viewModelProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.fragment_player_toolbar.*
import kotlinx.android.synthetic.main.player_controls.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class PlayerFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast { viewModelProvider<PlayerFragmentViewModel>(viewModelFactory) }
    @Inject
    lateinit var presenter: PlayerFragmentPresenter
    @Inject
    lateinit var navigator: Navigator

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var mediaProvider: MediaProvider

    private var seekBarDisposable: Disposable? = null

    private var lyricsDisposable: Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        val adapter = PlayerFragmentAdapter(
            lifecycle, activity as MediaProvider,
            navigator, viewModel, presenter
        )

        layoutManager = LinearLayoutManager(context)
        view.list.adapter = adapter
        view.list.layoutManager = layoutManager
        view.list.isNestedScrollingEnabled = false
        val callback = TouchHelperAdapterCallback(adapter, ItemTouchHelper.RIGHT/* or ItemTouchHelper.LEFT*/)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(view.list)
        adapter.touchHelper = touchHelper

        val statusBarAlpha = if (!isMarshmallow()) 1f else 0f
        view.statusBar?.alpha = statusBarAlpha

        if (AppTheme.isBigImageTheme()) {
            val set = ConstraintSet()
            set.clone(view as ConstraintLayout)
            set.connect(view.list.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            set.applyTo(view)
        }

        mediaProvider = (activity as MediaProvider)

        mediaProvider.observeQueue()
            .distinctUntilChanged()
            .map { it.map { it.toDisplayableItem() } }
            .map { queue ->
                if (!AppTheme.isMiniTheme()) {
                    val copy = queue.toMutableList()
                    if (copy.size > PlaylistConstants.MINI_QUEUE_SIZE - 1) {
                        copy.add(viewModel.footerLoadMore)
                    }
                    copy.add(0, viewModel.playerControls())
                    copy
                } else {
                    listOf(viewModel.playerControls())
                }
            }.subscribe(viewLifecycleOwner, viewModel::updateQueue)

        viewModel.observeMiniQueue()
            .subscribe(viewLifecycleOwner, adapter::updateDataSet)

        mediaProvider.observePlaybackState()
            .subscribe(viewLifecycleOwner) {
                val bookmark = it.extractBookmark()
                viewModel.updateProgress(bookmark)
                handleSeekBar(bookmark, it.isPlaying(), it.playbackSpeed)
            }

    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isCollapsed()) return

        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun handleSeekBar(bookmark: Int, isPlaying: Boolean, speed: Float) {
        seekBarDisposable.unsubscribe()

        if (isPlaying) {
            seekBarDisposable =
                Observable.interval(PROGRESS_BAR_INTERVAL.toLong(), TimeUnit.MILLISECONDS, Schedulers.computation())
                    .map { (it + 1) * PROGRESS_BAR_INTERVAL * speed + bookmark }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ viewModel.updateProgress(it.toInt()) }, Throwable::printStackTrace)
        }
    }

    override fun onResume() {
        super.onResume()
        shuffle?.setOnClickListener { mediaProvider.toggleShuffleMode() }
        repeat?.setOnClickListener { mediaProvider.toggleRepeatMode() }
        getSlidingPanel()?.addPanelSlideListener(slidingPanelListener)
    }

    override fun onPause() {
        super.onPause()
        shuffle?.setOnClickListener(null)
        repeat?.setOnClickListener(null)
        getSlidingPanel()?.removePanelSlideListener(slidingPanelListener)
    }

    override fun onStop() {
        super.onStop()
        seekBarDisposable.unsubscribe()
        lyricsDisposable.unsubscribe()
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
            isPlayable = true,
            trackNumber = "${this.queueId}"
        )
    }

    override fun provideLayoutId(): Int {
        return when {
            AppTheme.isFullscreenTheme() -> R.layout.fragment_player_fullscreen
            AppTheme.isCleanTheme() -> R.layout.fragment_player_clean
            AppTheme.isMiniTheme() -> R.layout.fragment_player_mini
            else -> R.layout.fragment_player
        }
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (!isMarshmallow() && slideOffset in .9f..1f) {
                val alpha = (1 - slideOffset) * 10
                statusBar?.alpha = clamp(abs(1 - alpha), 0f, 1f)
            }
            val alpha = clamp(slideOffset * 5f, 0f, 1f)
            view?.alpha = alpha
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                lyricsDisposable.unsubscribe()
                lyricsDisposable = Completable.timer(50, TimeUnit.MILLISECONDS, Schedulers.io())
                    .andThen(viewModel.showLyricsTutorialIfNeverShown())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ lyrics?.let { TutorialTapTarget.lyrics(it) } }, {})
            } else {
                lyricsDisposable.unsubscribe()
            }
        }
    }
}