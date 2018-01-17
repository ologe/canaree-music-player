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
import com.jakewharton.rxbinding2.view.RxView
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

class PlayerFragment : BaseFragment() {

    @Inject lateinit var viewModel: PlayerFragmentViewModel
    @Inject lateinit var musicController: MusicController
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var floatingInfoServiceBinder: FloatingInfoServiceClass
    @Inject lateinit var adapter : MiniQueueFragmentAdapter

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

        RxView.clicks(repeat)
                .asLiveData()
                .subscribe(this, { musicController.toggleRepeatMode() })

        RxView.clicks(shuffle)
                .asLiveData()
                .subscribe(this, { musicController.toggleShuffleMode() })

        RxView.clicks(fakeNext)
                .asLiveData()
                .subscribe(this, { musicController.skipToNext() })

        RxView.clicks(fakePrevious)
                .asLiveData()
                .subscribe(this, { musicController.skipToPrevious() })

        RxView.clicks(favorite)
                .asLiveData()
                .subscribe(this, { musicController.togglePlayerFavorite() })

        RxView.clicks(playingQueue)
                .asLiveData()
                .subscribe(this, { navigator.toPlayingQueueFragment() })

        RxView.clicks(floatingWindow)
                .asLiveData()
                .subscribe(this, {
                    FloatingInfoServiceHelper.startServiceOrRequestOverlayPermission(activity!!, floatingInfoServiceBinder)
                })

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
    }

    override fun onPause() {
        super.onPause()
        cover.setOnSwipeListener(null)
        view!!.list.removeOnScrollListener(listener)
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
            child?.let {
                val top = MathUtils.clamp(it.top, 0, Int.MAX_VALUE)
                val translation = view!!.slidingView.bottom - top

                val realTranslation = MathUtils.clamp(translation, 0, view!!.slidingView.bottom).toFloat()

                view!!.slidingView.translationY = -realTranslation
            }

        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_player
}