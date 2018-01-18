package dev.olog.presentation.fragment_player

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.math.MathUtils
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.GlideApp
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.model.CoverModel
import dev.olog.presentation.model.PlayerFragmentMetadata
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.subscribe
import dev.olog.presentation.utils.rx.RxSlidingUpPanel
import dev.olog.presentation.widgets.SwipeableImageView
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.extension.asLiveData
import dev.olog.shared_android.extension.isPortrait
import dev.olog.shared_android.interfaces.FloatingInfoServiceClass
import dev.olog.shared_android.rx.SeekBarObservable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.ofType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import kotlinx.android.synthetic.main.layout_player_toolbar.*
import org.jetbrains.anko.find
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class PlayerFragment : BaseFragment() {

    @Inject lateinit var viewModel: PlayerFragmentViewModel
    @Inject lateinit var musicController: MusicController
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var floatingInfoServiceBinder: FloatingInfoServiceClass
    @Inject lateinit var adapter : MiniQueueFragmentAdapter

    private val seekBarTouchInterceptor by lazy(NONE) { SeekBarTouchInterceptor(view!!.seekBar) }

    private val imageTouchInterceptor by lazy(NONE) { ImageTouchInterceptor(view!!.cover) }

    private val skipNextTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakeNext, { musicController.skipToNext() }) }

    private val skipPreviousTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { musicController.skipToPrevious() }) }

    private val floatingWindowTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { FloatingInfoServiceHelper.startServiceOrRequestOverlayPermission(activity!!, floatingInfoServiceBinder) }) }

    private val favoriteTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { musicController.togglePlayerFavorite() }) }

    private val playingQueueTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { navigator.toPlayingQueueFragment() }) }

    private val shuffleTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { musicController.toggleShuffleMode() }) }

    private val repeatTouchInterceptor by lazy(NONE) {
        SimpleViewClickInterceptor(view!!.fakePrevious, { musicController.toggleRepeatMode() }) }

    private lateinit var layoutManager: LinearLayoutManager

    private var updateDisposable : Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.miniQueue.subscribe(this, {
            adapter.updateDataSet(it)
        })

        viewModel.onMetadataChangedLiveData
                .subscribe(this, this::setMetadata)

        viewModel.onCoverChangedLiveData
                .subscribe(this, this::setCover)

        viewModel.onPlaybackStateChangedLiveData
                .subscribe(this, {
                    handleSeekbarState(it)
                    nowPlaying.isActivated = it
                    cover.isActivated = it
                    coverLayout.isActivated = it
                })

        viewModel.onRepeatModeChangedLiveData
                .subscribe(this, {
                    repeat.setImageResource(if (it == PlaybackStateCompat.REPEAT_MODE_ONE)
                        R.drawable.vd_repeat_one else R.drawable.vd_repeat)
                    repeat.setColorFilter(ContextCompat.getColor(context!!,
                            if (it == PlaybackStateCompat.REPEAT_MODE_NONE) R.color.button_primary_tint
                            else R.color.item_selected))
                })

        viewModel.onShuffleModeChangedLiveData
                .subscribe(this , {
                    shuffle.setColorFilter(ContextCompat.getColor(context!!,
                            if (it == PlaybackStateCompat.SHUFFLE_MODE_NONE) R.color.button_primary_tint
                            else R.color.item_selected))
                })

        viewModel.onMaxChangedObservable
                .subscribe(this, {
                    duration.text = it.asString
                    seekBar.max = it.asInt
                })

//        bookmark textView will automatically updated
        viewModel.onBookmarkChangedObservable
                .subscribe(this, { seekBar.progress = it })

        viewModel.onFavoriteStateChangedObservable
                .subscribe(this, { favorite.toggleFavorite(it) })

        viewModel.onFavoriteAnimateRequestObservable
                .subscribe(this, { favorite.animateFavorite(it) })

        val seekBarObservable = SeekBarObservable(seekBar).share()

        seekBarObservable
                .ofType<Int>()
                .map { it.toLong() }
                .map { TextUtils.getReadableSongLength(it) }
                .asLiveData()
                .subscribe(this, {
                    bookmark.text = it
                })

        seekBarObservable.ofType<Pair<SeekBarObservable.Notification, Int>>()
                .filter { (notification, _) -> notification == SeekBarObservable.Notification.STOP }
                .map { (_, progress) -> progress.toLong() }
                .asLiveData()
                .subscribe(this, musicController::seekTo)

        RxSlidingUpPanel.panelStateEvents(activity!!.slidingPanel)
                .map { it.newState == SlidingUpPanelLayout.PanelState.EXPANDED  }
                .asLiveData()
                .subscribe(this, {
                    view!!.slidingView.artist.isSelected = it
                    view!!.slidingView.find<TextView>(R.id.title).isSelected = it
                })
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.slidingView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                view.slidingView.viewTreeObserver.removeOnPreDrawListener(this)
                view.list.setPadding(view.list.paddingLeft, view.slidingView.bottom,
                        view.list.paddingRight, view.list.paddingBottom)
                layoutManager = LinearLayoutManager(context)
                view.list.setHasFixedSize(true)
                view.list.layoutManager = layoutManager
                view.list.adapter = adapter
                adapter.touchHelper()!!.attachToRecyclerView(view.list)
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        cover.setOnSwipeListener(object : SwipeableImageView.SwipeListener {

            override fun onSwipedLeft() {
                musicController.skipToNext()
            }

            override fun onSwipedRight() {
                musicController.skipToPrevious()
            }

            override fun onClick() {
                musicController.playPause()
            }
        })
        view!!.list.addOnScrollListener(listener)
        activity!!.slidingPanel.setScrollableView(view!!.list)
        if (activity!!.isPortrait){
            view!!.list.addOnItemTouchListener(seekBarTouchInterceptor)
            view!!.list.addOnItemTouchListener(imageTouchInterceptor)
            view!!.list.addOnItemTouchListener(skipNextTouchInterceptor)
            view!!.list.addOnItemTouchListener(skipPreviousTouchInterceptor)
            view!!.list.addOnItemTouchListener(repeatTouchInterceptor)
            view!!.list.addOnItemTouchListener(shuffleTouchInterceptor)
        } else {
            view!!.fakeNext.setOnClickListener { musicController.skipToNext() }
            view!!.fakePrevious.setOnClickListener { musicController.skipToPrevious() }
            view!!.shuffle.setOnClickListener { musicController.toggleShuffleMode() }
            view!!.repeat.setOnClickListener { musicController.toggleRepeatMode() }
        }
        view!!.list.addOnItemTouchListener(floatingWindowTouchInterceptor)
        view!!.list.addOnItemTouchListener(favoriteTouchInterceptor)
        view!!.list.addOnItemTouchListener(playingQueueTouchInterceptor)
    }

    override fun onPause() {
        super.onPause()
        cover.setOnSwipeListener(null)
        view!!.list.removeOnScrollListener(listener)
        if (activity!!.isPortrait){
            view!!.list.removeOnItemTouchListener(seekBarTouchInterceptor)
            view!!.list.removeOnItemTouchListener(imageTouchInterceptor)
            view!!.list.removeOnItemTouchListener(skipNextTouchInterceptor)
            view!!.list.removeOnItemTouchListener(skipPreviousTouchInterceptor)
            view!!.list.removeOnItemTouchListener(repeatTouchInterceptor)
            view!!.list.removeOnItemTouchListener(shuffleTouchInterceptor)
        } else {
            view!!.fakeNext.setOnClickListener(null)
            view!!.fakePrevious.setOnClickListener(null)
            view!!.shuffle.setOnClickListener(null)
            view!!.repeat.setOnClickListener(null)
        }
        view!!.list.removeOnItemTouchListener(floatingWindowTouchInterceptor)
        view!!.list.removeOnItemTouchListener(favoriteTouchInterceptor)
        view!!.list.removeOnItemTouchListener(playingQueueTouchInterceptor)
    }

    override fun onStop() {
        super.onStop()
        updateDisposable.unsubscribe()
    }

    private fun handleSeekbarState(isPlaying: Boolean){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar()
        }
    }

    private fun resumeSeekBar(){
        updateDisposable = Observable.interval(250, TimeUnit.MILLISECONDS)
                .subscribe({ view!!.seekBar.incrementProgressBy(250) }, Throwable::printStackTrace)
    }

    private fun setMetadata(metadata: PlayerFragmentMetadata){
        view!!.slidingView.findViewById<TextView>(R.id.title).text = metadata.title
        view!!.slidingView.artist.text = metadata.artist
        view!!.explicit.visibility = if (metadata.isExplicit) View.VISIBLE else View.GONE
        view!!.remix.visibility = if (metadata.isRemix) View.VISIBLE else View.GONE
    }

    private fun setCover(coverModel: CoverModel){
        val (img, placeholder) = coverModel

        GlideApp.with(context!!).clear(cover)

        GlideApp.with(context!!)
                .load(img)
                .centerCrop()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .priority(Priority.IMMEDIATE)
                .override(500)
                .into(cover)
    }

    private val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val child = recyclerView.getChildAt(0)
            val translation = child?.let {
                val top = MathUtils.clamp(it.top, 0, Int.MAX_VALUE)
                val translation = view!!.slidingView.bottom - top

                val realTranslation = MathUtils.clamp(translation, 0, view!!.slidingView.bottom).toFloat()

                -realTranslation
            } ?: 0f
            view!!.slidingView.translationY = translation
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_player
}