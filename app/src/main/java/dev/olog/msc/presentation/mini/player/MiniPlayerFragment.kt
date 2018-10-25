package dev.olog.msc.presentation.mini.player

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.core.math.MathUtils
import com.jakewharton.rxbinding2.view.RxView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.android.synthetic.main.fragment_mini_player.view.*
import kotlinx.android.synthetic.main.item_tab_shuffle.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragment : BaseFragment(), SlidingUpPanelLayout.PanelSlideListener{

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val PROGRESS_BAR_INTERVAL = 250L
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    @Inject lateinit var presenter: MiniPlayerFragmentPresenter

    private var seekBarDisposable: Disposable? = null

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }
        val (modelTitle, modelSubtitle) = presenter.getMetadata()
        view.title.text = modelTitle
        view.artist.text = DisplayableItem.adjustArtist(modelSubtitle)

        val media = activity as MediaProvider

        view.coverWrapper.toggleVisibility(AppTheme.isMiniTheme(), true)

        media.onMetadataChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    title.text = it.getTitle()
                    presenter.startShowingLeftTime(it.isPodcast(), it.getDuration())
                    if (!it.isPodcast()){
                        artist.text = it.getArtist()
                    }
                    updateProgressBarMax(it.getDuration())
                    updateImage(it)
                }

        presenter.observeProgress
                .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
                .filter { view.artist.text != text }
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    artist.text = it
                }

        media.onStateChanged()
                .filter { it.isPlaying()|| it.isPaused() }
                .distinctUntilChanged()
                .asLiveData()
                .subscribe(viewLifecycleOwner) {
                    updateProgressBarProgress(it.position)
                    handleProgressBar(it.isPlaying(), it.playbackSpeed)
                }

        media.onStateChanged()
                .map { it.state }
                .filter { it == PlaybackStateCompat.STATE_PLAYING ||
                        it == PlaybackStateCompat.STATE_PAUSED
                }.distinctUntilChanged()
                .asLiveData()
                .subscribe(viewLifecycleOwner) { state ->

                    if (state == PlaybackStateCompat.STATE_PLAYING){
                        playAnimation(true)
                    } else {
                        pauseAnimation(true)
                    }
                }

        media.onStateChanged()
                .map { it.state }
                .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ||
                        state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
                .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
                .asLiveData()
                .subscribe(viewLifecycleOwner, this::animateSkipTo)

        RxView.clicks(view.next)
                .asLiveData()
                .subscribe(viewLifecycleOwner) { media.skipToNext() }

        RxView.clicks(view.playPause)
                .asLiveData()
                .subscribe(viewLifecycleOwner) { media.playPause() }

        RxView.clicks(view.previous)
                .asLiveData()
                .subscribe(viewLifecycleOwner) { media.skipToPrevious() }

        presenter.skipToNextVisibility
                .subscribe(viewLifecycleOwner) {
                    view.next.updateVisibility(it)
                }

        presenter.skipToPreviousVisibility
                .subscribe(viewLifecycleOwner) {
                    view.previous.updateVisibility(it)
                }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(this)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.toggleVisibility(!getSlidingPanel().isExpanded(), true)
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

    private fun updateImage(metadata: MediaMetadataCompat){
        if (!AppTheme.isMiniTheme()){
            return
        }
        bigCover.loadImage(metadata)
    }

    private fun handleProgressBar(isPlaying: Boolean, speed: Float) {
        seekBarDisposable.unsubscribe()
        if (isPlaying) {
            resumeProgressBar(speed)
        }
    }

    private fun resumeProgressBar(speed: Float) {
        seekBarDisposable = Observable
                .interval(PROGRESS_BAR_INTERVAL, TimeUnit.MILLISECONDS, Schedulers.computation())
                .subscribe({
                    progressBar.incrementProgressBy((PROGRESS_BAR_INTERVAL * speed).toInt())
                    presenter.updateProgress((progressBar.progress + (PROGRESS_BAR_INTERVAL * speed)).toLong())
                }, Throwable::printStackTrace)
    }


    override fun onPanelSlide(panel: View?, slideOffset: Float) {
        view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
        view?.toggleVisibility(slideOffset <= .8f, true)
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}