package dev.olog.feature.player.mini

import android.os.Bundle
import android.view.View
import androidx.core.math.MathUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.slidingPanel
import dev.olog.feature.player.R
import dev.olog.media.mediaProvider
import dev.olog.media.model.PlayerState
import dev.olog.shared.android.extensions.*
import dev.olog.shared.widgets.extension.expand
import dev.olog.shared.widgets.extension.isCollapsed
import dev.olog.shared.widgets.extension.isExpanded
import kotlinx.android.synthetic.main.fragment_mini_player.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AndroidEntryPoint
class MiniPlayerFragment : BaseFragment(){

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    @Inject
    lateinit var presenter: MiniPlayerFragmentPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }

        presenter.getMetadata()?.let {
            title.text = it.title
            artist.text = it.artist
        }

        mediaProvider.observeMetadata()
                .subscribe(viewLifecycleOwner) {
                    title.text = it.title
                    presenter.startShowingLeftTime(it.isPodcast, it.duration)
                    if (!it.isPodcast){
                        artist.text = it.artist
                    }
                    updateProgressBarMax(it.duration)
                }

        mediaProvider.observePlaybackState()
                .filter { it.isPlaying|| it.isPaused }
                .distinctUntilChanged()
                .subscribe(viewLifecycleOwner) { progressBar.onStateChanged(it) }

        presenter.observePodcastProgress(progressBar.observeProgress())
            .map { resources.getQuantityString(localization.R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
            .collectOnLifecycle(this) { artist.text = it }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .subscribe(viewLifecycleOwner) { state ->
                when (state){
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }

        mediaProvider.observePlaybackState()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .subscribe(viewLifecycleOwner, this::animateSkipTo)

        presenter.skipToNextVisibility
                .subscribe(viewLifecycleOwner) {
                    next.updateVisibility(it)
                }

        presenter.skipToPreviousVisibility
                .subscribe(viewLifecycleOwner) {
                    previous.updateVisibility(it)
                }
    }

    override fun onResume() {
        super.onResume()
        slidingPanel.addBottomSheetCallback(slidingPanelListener)
        view?.setOnClickListener { slidingPanel.expand() }
        view?.toggleVisibility(!slidingPanel.isExpanded(), true)
        next.setOnClickListener { mediaProvider.skipToNext() }
        playPause.setOnClickListener { mediaProvider.playPause() }
        previous.setOnClickListener { mediaProvider.skipToPrevious() }
    }

    override fun onPause() {
        super.onPause()
        slidingPanel.removeBottomSheetCallback(slidingPanelListener)
        view?.setOnClickListener(null)
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
            view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            view?.toggleVisibility(slideOffset <= .8f, true)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun provideLayoutId(): Int = R.layout.fragment_mini_player
}