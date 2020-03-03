package dev.olog.presentation.playermini

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.media.model.PlayerState
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerMetadata
import dev.olog.presentation.BindingsAdapter
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.utils.TextUpdateTransition
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.android.extensions.*
import dev.olog.shared.android.theme.BottomSheetType
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.lazyFast
import kotlinx.android.synthetic.main.fragment_mini_player.artist
import kotlinx.android.synthetic.main.fragment_mini_player.next
import kotlinx.android.synthetic.main.fragment_mini_player.playPause
import kotlinx.android.synthetic.main.fragment_mini_player.previous
import kotlinx.android.synthetic.main.fragment_mini_player.progressBar
import kotlinx.android.synthetic.main.fragment_mini_player.textWrapper
import kotlinx.android.synthetic.main.fragment_mini_player.title
import kotlinx.android.synthetic.main.fragment_mini_player_floating.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

@Keep
class MiniPlayerFragment : BaseFragment(){

    companion object {
        @JvmStatic
        private val TAG = MiniPlayerFragment::class.java.name
        private const val BUNDLE_IS_VISIBLE = "bundle__is_visible"
    }

    @Inject lateinit var presenter: MiniPlayerFragmentPresenter

    private val media by lazyFast { requireActivity() as MediaProvider }

    private var updateTitlesJob by autoDisposeJob()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }
        val lastMetadata = presenter.getMetadata()
        title.text = lastMetadata.title
        artist.text = lastMetadata.subtitle

        media.observeMetadata()
                .subscribe(viewLifecycleOwner) {
                    cover?.let { view ->
                        BindingsAdapter.loadSongImage(view, it.mediaId)
                    }

                    presenter.startShowingLeftTime(it.isPodcast, it.duration)
                    updateTitlesJob = launchWhenResumed { updateTitles(it) }

                    updateProgressBarMax(it.duration)
                }

        media.observePlaybackState()
                .filter { it.isPlaying|| it.isPaused }
                .distinctUntilChanged()
                .subscribe(viewLifecycleOwner) { progressBar.onStateChanged(it) }

        presenter.observePodcastProgress(progressBar.observeProgress())
            .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
            .filter { timeLeft -> artist.text != timeLeft } // check (new time left != old time left
            .onEach { artist.text = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        media.observePlaybackState()
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

        media.observePlaybackState()
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

    private suspend fun updateTitles(metadata: PlayerMetadata) {
        title.isSelected = false
        artist.isSelected = false

        TransitionManager.beginDelayedTransition(textWrapper, TextUpdateTransition)
        title.text = metadata.title
        if (!metadata.isPodcast){
            artist.text = metadata.artist
        }

        delay(TextUpdateTransition.DURATION * 2)
        title.isSelected = true
        artist.isSelected = true
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addBottomSheetCallback(slidingPanelListener)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.toggleVisibility(!getSlidingPanel().isExpanded(), true)
        next.setOnClickListener { media.skipToNext() }
        playPause.setOnClickListener { media.playPause() }
        previous.setOnClickListener { media.skipToPrevious() }
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removeBottomSheetCallback(slidingPanelListener)
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
            view?.toggleVisibility(slideOffset <= .8f, true)
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