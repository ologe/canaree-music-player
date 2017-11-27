package dev.olog.presentation.fragment_mini_player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.presentation.R
import dev.olog.presentation._base.BaseFragment
import dev.olog.presentation.isCollapsed
import dev.olog.presentation.isExpanded
import dev.olog.presentation.music_service.MusicController
import dev.olog.presentation.utils.asLiveData
import dev.olog.presentation.utils.subscribe
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_mini_player.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment(){

    companion object {
        private const val PROGRESS_BAR_INTERVAL = 250L
    }

    @Inject lateinit var viewModel: MiniPlayerFragmentViewModel
    @Inject lateinit var musicController: MusicController

    private var seekBarDisposable: Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.onMetadataChangedLiveData
                .subscribe(this, {
                    view!!.title.text = it.title
                    view!!.artist.text = it.subtitle
                })

        viewModel.animatePlayPauseLiveData
                .subscribe(this, {
                    if (it == PlaybackStateCompat.STATE_PLAYING) {
                        playAnimation(true)
                    } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                        pauseAnimation(true)
                    }
                })

        viewModel.animateSkipToLiveData
                .subscribe(this, this::animateSkipTo)

        viewModel.onBookmarkChangedLiveData
                .subscribe(this, this::updateProgressBarProgress)


        viewModel.onMaxChangedLiveData
                .subscribe(this, this::updateProgressBarMax)

        viewModel.handleProgressBarLiveData
                .subscribe(this, this::handleProgressBar)

        RxView.clicks(view!!.next)
                .asLiveData()
                .subscribe(this, { musicController.skipToNext() })

        RxView.clicks(view!!.previous)
                .asLiveData()
                .subscribe(this, { musicController.skipToPrevious() })

        RxView.clicks(view!!.playPause)
                .asLiveData()
                .subscribe(this, { musicController.playPause() })
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()?.addPanelSlideListener(panelSlideListener)
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()?.removePanelSlideListener(panelSlideListener)
    }

    private val panelSlideListener = object : SlidingUpPanelLayout.SimplePanelSlideListener() {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            view!!.alpha = Math.max(0f, 1 - slideOffset * 2f)
            view!!.visibility = if (slideOffset > .8f) View.GONE else View.VISIBLE
        }
    }

    private fun playAnimation(animate: Boolean) {
        view!!.playPause.animationPlay(getSlidingPanel().isCollapsed() && animate)
    }

    private fun pauseAnimation(animate: Boolean) {
        view!!.playPause.animationPause(getSlidingPanel().isCollapsed() && animate)
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            view!!.next.playAnimation()
        } else {
            view!!.previous.playAnimation()
        }
    }

    private fun updateProgressBarProgress(progress: Long) {
        view!!.progressBar.progress = progress.toInt()
    }

    private fun updateProgressBarMax(max: Long) {
        view!!.progressBar.max = max.toInt()
    }

    private fun handleProgressBar(isPlaying: Boolean) {
        seekBarDisposable.unsubscribe()
        if (isPlaying) {
            resumeProgressBar()
        }
    }

    private fun resumeProgressBar() {
        seekBarDisposable = Observable
                .interval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe { view!!.progressBar.incrementProgressBy(PROGRESS_BAR_INTERVAL.toInt()) }
    }

    override fun provideView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mini_player, container, false)
    }
}