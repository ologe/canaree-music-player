package dev.olog.msc.presentation.mini.player

import android.os.Bundle
import android.support.v4.math.MathUtils
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.android.synthetic.main.fragment_mini_player.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment(), SlidingUpPanelLayout.PanelSlideListener{

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val PROGRESS_BAR_INTERVAL = 250L
        private const val BUNDLE_IS_VISIBLE = TAG + ".BUNDLE_IS_VISIBLE"
    }

    @Inject lateinit var viewModel: MiniPlayerFragmentPresenter
    @Inject lateinit var presenter: MiniPlayerPresenter

    private var seekBarDisposable: Disposable? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val media = activity as MediaProvider

        media.onMetadataChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(this, {
                    title.text = it.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    artist.text = it.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    updateProgressBarMax(it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                })

        media.onStateChanged()
                .filter { it.state == PlaybackStateCompat.STATE_PLAYING ||
                        it.state == PlaybackStateCompat.STATE_PAUSED
                }.distinctUntilChanged()
                .asLiveData()
                .subscribe(this, {
                    val state = it.state
                    updateProgressBarProgress(it.position)
                    handleProgressBar(state == PlaybackStateCompat.STATE_PLAYING)
                })

        media.onStateChanged()
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

        media.onStateChanged()
                .map { it.state }
                .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                        state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
                .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                .asLiveData()
                .subscribe(this, this::animateSkipTo)

        RxView.clicks(next)
                .asLiveData()
                .subscribe(this, { media.skipToNext() })

        RxView.clicks(playPause)
                .asLiveData()
                .subscribe(this, { media.playPause() })

        RxView.clicks(previous)
                .asLiveData()
                .subscribe(this, { media.skipToPrevious() })

        viewModel.skipToNextVisibility
                .subscribe(this, view!!.next::toggleVisibility)

        viewModel.skipToPreviousVisibility
                .subscribe(this, view!!.previous::toggleVisibility)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE))
        }
        view.title.text = presenter.getLastTitle()
        view.artist.text = presenter.getLastSubtitle()
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(this)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removePanelSlideListener(this)
        view?.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        seekBarDisposable.unsubscribe()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation(animate: Boolean) {
        playPause.animationPlay(getSlidingPanel().isCollapsed() && animate)
    }

    private fun pauseAnimation(animate: Boolean) {
        playPause.animationPause(getSlidingPanel().isCollapsed() && animate)
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
                .subscribe({
                    view!!.progressBar.incrementProgressBy(PROGRESS_BAR_INTERVAL.toInt())
                }, Throwable::printStackTrace)
    }


    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
        view?.visibility = if (slideOffset > .8f) View.GONE else View.VISIBLE
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}