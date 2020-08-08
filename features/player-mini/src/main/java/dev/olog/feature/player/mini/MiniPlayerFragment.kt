package dev.olog.feature.player.mini

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.lib.media.model.PlayerState
import dev.olog.feature.presentation.base.activity.BaseFragment
import dev.olog.feature.presentation.base.extensions.expand
import dev.olog.feature.presentation.base.extensions.isCollapsed
import dev.olog.feature.presentation.base.extensions.isExpanded
import dev.olog.feature.presentation.base.loadSongImage
import dev.olog.shared.android.extensions.themeManager
import dev.olog.shared.android.theme.BottomSheetType
import kotlinx.android.synthetic.main.fragment_mini_player.artist
import kotlinx.android.synthetic.main.fragment_mini_player.progressBar
import kotlinx.android.synthetic.main.fragment_mini_player.textWrapper
import kotlinx.android.synthetic.main.fragment_mini_player.title
import kotlinx.android.synthetic.main.fragment_mini_player_floating.*
import kotlinx.android.synthetic.main.fragment_mini_player_floating.buttons
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Keep
@AndroidEntryPoint
internal class MiniPlayerFragment : BaseFragment() {

    companion object {
        @JvmStatic
        private val TAG = MiniPlayerFragment::class.java.name
        private const val BUNDLE_IS_VISIBLE = "bundle__is_visible"
    }

    @Inject
    lateinit var presenter: MiniPlayerFragmentPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            view.isVisible = it.getBoolean(BUNDLE_IS_VISIBLE)
        }
        val lastMetadata = presenter.getMetadata()
        title.text = lastMetadata.title
        artist.text = lastMetadata.subtitle

        mediaProvider.observeMetadata()
            .onEach {
                buttons.onTrackChanged(it.isPodcast)

                cover?.loadSongImage(it.mediaId)
                presenter.startShowingLeftTime(it.isPodcast, it.duration)

                updateTitles(it)
                updateProgressBarMax(it.duration)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        mediaProvider.observePlaybackState()
            .filter { it.isPlaying || it.isPaused }
            .distinctUntilChanged()
            .onEach { progressBar.onStateChanged(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        presenter.observePodcastProgress(progressBar.observeProgress())
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
            .onEach { artist.text = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        mediaProvider.observePlaybackState()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .onEach { animateSkipTo(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        presenter.skipToNextVisibility
            .onEach { buttons.toggleNextButton(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        presenter.skipToPreviousVisibility
            .onEach { buttons.togglePreviousButton(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateTitles(metadata: PlayerMetadata) {
        val artist = if (metadata.isPodcast) artist.text.toString() else metadata.artist
        textWrapper.update(metadata.title, artist)
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addBottomSheetCallback(slidingPanelListener)
        requireView().setOnClickListener { getSlidingPanel()?.expand() }
        requireView().isVisible = !getSlidingPanel().isExpanded()
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removeBottomSheetCallback(slidingPanelListener)
        requireView().setOnClickListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation() {
        buttons.startPlayAnimation(getSlidingPanel().isCollapsed())
    }

    private fun pauseAnimation() {
        buttons.startPauseAnimation(getSlidingPanel().isCollapsed())
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            buttons.startSkipNextAnimation()
        } else {
            buttons.startSkipPreviousAnimation()
        }
    }

    private fun updateProgressBarMax(max: Long) {
        progressBar.max = max.toInt()
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            requireView().alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            requireView().isVisible = slideOffset <= .8f
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun provideLayoutId(): Int {
        return when (themeManager.bottomSheetType) {
            BottomSheetType.DEFAULT -> R.layout.fragment_mini_player
            BottomSheetType.FLOATING -> R.layout.fragment_mini_player_floating
        }
    }
}