package dev.olog.presentation.playermini

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerState
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Keep
@AndroidEntryPoint
class MiniPlayerFragment : BaseFragment(){

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    private val viewModel by viewModels<MiniPlayerFragmentViewModel>()

    private val media by lazyFast { requireActivity().findInContext<MediaProvider>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.isVisible = it.getBoolean(BUNDLE_IS_VISIBLE)
        }
        val lastMetadata = viewModel.getMetadata()
        title.text = lastMetadata.title
        artist.text = lastMetadata.subtitle

        media.observeMetadata()
                .collectOnViewLifecycle(this) {
                    title.text = it.title
                    viewModel.startShowingLeftTime(it.isPodcast, it.duration)
                    if (!it.isPodcast){
                        artist.text = it.artist
                    }
                    updateProgressBarMax(it.duration)
                }

        media.observePlaybackState()
                .filter { it.isPlaying|| it.isPaused }
                .distinctUntilChanged()
                .collectOnViewLifecycle(this) { progressBar.onStateChanged(it) }

        viewModel.observePodcastProgress(progressBar.observeProgress())
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
            .collectOnViewLifecycle(this) {
                artist.text = it
            }

        media.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .collectOnViewLifecycle(this) { state ->
                when (state){
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }

        media.observePlaybackState()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .collectOnViewLifecycle(this) {
                animateSkipTo(it)
            }

        viewModel.skipToNextVisibility
                .subscribe(viewLifecycleOwner) {
                    next.updateVisibility(it)
                }

        viewModel.skipToPreviousVisibility
                .subscribe(viewLifecycleOwner) {
                    previous.updateVisibility(it)
                }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(slidingPanelListener)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.isVisible = !getSlidingPanel().isExpanded()
        next.setOnClickListener { media.skipToNext() }
        playPause.setOnClickListener { media.playPause() }
        previous.setOnClickListener { media.skipToPrevious() }
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removePanelSlideListener(slidingPanelListener)
        view?.setOnClickListener(null)
        next.setOnClickListener(null)
        playPause.setOnClickListener(null)
        previous.setOnClickListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation() {
        playPause.animationPlay(getSlidingPanel().isCollapsed())
    }

    private fun pauseAnimation() {
        playPause.animationPause(getSlidingPanel().isCollapsed())
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun updateProgressBarMax(max: Long) {
        progressBar.max = max.toInt()
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            view?.isVisible = slideOffset <= .8f
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}