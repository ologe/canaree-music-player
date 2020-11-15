package dev.olog.presentation.playermini

import android.os.Bundle
import android.view.View
import androidx.core.math.MathUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.media.mediaProvider
import dev.olog.media.model.PlayerState
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.android.extensions.*
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class MiniPlayerFragment : BaseFragment(){

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    @Inject lateinit var presenter: MiniPlayerFragmentPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }
        val lastMetadata = presenter.getMetadata()
        title.text = lastMetadata.title
        artist.text = lastMetadata.subtitle

        requireActivity().mediaProvider.metadata
            .onEach {
                title.text = it.title
                presenter.startShowingLeftTime(it.isPodcast, it.duration)
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

        launch {
            presenter.observePodcastProgress(progressBar.observeProgress())
                .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
                .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
                .collect { artist.text = it }
        }

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

        presenter.skipToNextVisibility
            .onEach(next::updateVisibility)
            .launchIn(this)

        presenter.skipToPreviousVisibility
            .onEach(previous::updateVisibility)
            .launchIn(this)
    }

    override fun onResume() {
        super.onResume()
        slidingPanel.addBottomSheetCallback(slidingPanelListener)
        requireView().setOnClickListener {
            slidingPanel.expand()
        }
        requireView().toggleVisibility(!slidingPanel.isExpanded(), true)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, slidingPanel.isCollapsed())
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

    private fun updateProgressBarMax(max: Long) {
        progressBar.max = max.toInt()
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            requireView().alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            requireView().toggleVisibility(slideOffset <= .8f, true)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}