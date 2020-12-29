package dev.olog.feature.player.mini

import android.os.Bundle
import android.view.View
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.player.R
import dev.olog.lib.media.mediaProvider
import dev.olog.lib.media.model.PlayerState
import dev.olog.shared.android.expand
import dev.olog.shared.android.extensions.launchIn
import dev.olog.shared.android.isCollapsed
import dev.olog.shared.android.isExpanded
import dev.olog.shared.android.slidingPanel
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration

@AndroidEntryPoint
internal class PlayerMiniFragment : Fragment(R.layout.fragment_mini_player) {

    private val viewModel by viewModels<PlayerMiniFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lastMetadata = viewModel.getMetadata()
        title.text = lastMetadata.title
        artist.text = lastMetadata.subtitle

        requireActivity().mediaProvider.metadata
            .onEach {
                title.text = it.title
                viewModel.startShowingLeftTime(it.isPodcast, it.duration)
                if (!it.isPodcast){
                    artist.text = it.artist
                }
                updateProgressBarMax(it.duration)
            }.launchIn(this)

        requireActivity().mediaProvider.playbackState
            .filter { it.isPlaying|| it.isPaused }
            .distinctUntilChanged()
            .onEach { progressBar.onStateChanged(it) }
            .launchIn(this)

        viewModel.observePodcastProgress(progressBar.observeProgress())
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
            .onEach { artist.text = it }
            .launchIn(this)

        requireActivity().mediaProvider.playbackState
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach { state ->
                when (state){
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> error("invalid state $state")
                }
            }.launchIn(this)

        requireActivity().mediaProvider.playbackState
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .onEach(this::animateSkipTo)
            .launchIn(this)

        viewModel.skipToNextVisibility
            .onEach(next::updateVisibility)
            .launchIn(this)

        viewModel.skipToPreviousVisibility
            .onEach(previous::updateVisibility)
            .launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        slidingPanel.addBottomSheetCallback(slidingPanelListener)
        requireView().setOnClickListener {
            slidingPanel.expand()
        }
        requireView().isVisible = !slidingPanel.isExpanded()
        next.setOnClickListener {
            requireActivity().mediaProvider.skipToNext()
        }
        playPause.setOnClickListener {
            requireActivity().mediaProvider.playPause()
        }
        previous.setOnClickListener {
            requireActivity().mediaProvider.skipToPrevious()
        }
    }

    override fun onPause() {
        super.onPause()
        slidingPanel.removeBottomSheetCallback(slidingPanelListener)
        requireView().setOnClickListener(null)
        next.setOnClickListener(null)
        playPause.setOnClickListener(null)
        previous.setOnClickListener(null)
    }

    private fun playAnimation() {
        playPause.animationPlay(slidingPanel.isCollapsed())
    }

    private fun pauseAnimation() {
        playPause.animationPause(slidingPanel.isCollapsed())
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (slidingPanel.isExpanded()) return

        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun updateProgressBarMax(max: Duration) {
        progressBar.max = max.toLongMilliseconds().toInt()
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            requireView().alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            requireView().isVisible = slideOffset <= .8f
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

}