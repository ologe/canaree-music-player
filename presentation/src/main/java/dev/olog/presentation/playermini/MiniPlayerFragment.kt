package dev.olog.presentation.playermini

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.media.MediaProvider
import dev.olog.media.model.PlayerState
import dev.olog.presentation.R
import dev.olog.presentation.base.getSlidingPanel
import dev.olog.presentation.base.viewLifecycleScope
import dev.olog.presentation.databinding.FragmentMiniPlayerBinding
import dev.olog.presentation.utils.expand
import dev.olog.presentation.utils.isCollapsed
import dev.olog.presentation.utils.isExpanded
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.android.extensions.filter
import dev.olog.shared.android.extensions.map
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.android.extensions.toggleVisibility
import dev.olog.shared.android.viewBinding
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Keep
@AndroidEntryPoint
class MiniPlayerFragment : Fragment(R.layout.fragment_mini_player){

    companion object {
        private const val TAG = "MiniPlayerFragment"
        private const val BUNDLE_IS_VISIBLE = "$TAG.BUNDLE_IS_VISIBLE"
    }

    private val viewModel by viewModels<MiniPlayerFragmentViewModel>()

    private val media by lazyFast { requireActivity() as MediaProvider }

    private val binding by viewBinding(FragmentMiniPlayerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            view.toggleVisibility(it.getBoolean(BUNDLE_IS_VISIBLE), true)
        }
        val lastMetadata = viewModel.getMetadata()
        binding.title.text = lastMetadata.title
        binding.artist.text = lastMetadata.subtitle

        media.observeMetadata()
            .filterNotNull()
                .onEach {
                    binding.title.text = it.title
                    viewModel.startShowingLeftTime(it.isPodcast, it.duration)
                    if (!it.isPodcast){
                        binding.artist.text = it.artist
                    }
                    updateProgressBarMax(it.duration)
                }.launchIn(viewLifecycleScope)

        media.observePlaybackState()
            .filterNotNull()
                .filter { it.isPlaying|| it.isPaused }
                .distinctUntilChanged()
                .onEach { binding.progressBar.onStateChanged(it) }
                .launchIn(viewLifecycleScope)

        viewLifecycleScope.launch {
            viewModel.observePodcastProgress(binding.progressBar.observeProgress())
                .map { resources.getQuantityString(R.plurals.mini_player_time_left, it.toInt(), it) }
                .filter { timeLeft -> binding.artist.text != timeLeft } // check (new time left != old time left
                .collect { binding.artist.text = it }
        }

        media.observePlaybackState()
            .filterNotNull()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach { state ->
                when (state){
                    PlayerState.PLAYING -> playAnimation()
                    PlayerState.PAUSED -> pauseAnimation()
                    else -> throw IllegalArgumentException("invalid state $state")
                }
            }.launchIn(viewLifecycleScope)

        media.observePlaybackState()
            .filterNotNull()
            .filter { it.isSkipTo }
            .map { it.state == PlayerState.SKIP_TO_NEXT }
            .onEach(this::animateSkipTo)
            .launchIn(viewLifecycleScope)

        viewModel.skipToNextVisibility
                .subscribe(viewLifecycleOwner) {
                    binding.next.updateVisibility(it)
                }

        viewModel.skipToPreviousVisibility
                .subscribe(viewLifecycleOwner) {
                    binding.previous.updateVisibility(it)
                }
    }

    override fun onResume() {
        super.onResume()
        getSlidingPanel()!!.addPanelSlideListener(slidingPanelListener)
        view?.setOnClickListener { getSlidingPanel()?.expand() }
        view?.toggleVisibility(!getSlidingPanel().isExpanded(), true)
        binding.next.setOnClickListener { media.skipToNext() }
        binding.playPause.setOnClickListener { media.playPause() }
        binding.previous.setOnClickListener { media.skipToPrevious() }
    }

    override fun onPause() {
        super.onPause()
        getSlidingPanel()!!.removePanelSlideListener(slidingPanelListener)
        view?.setOnClickListener(null)
        binding.next.setOnClickListener(null)
        binding.playPause.setOnClickListener(null)
        binding.previous.setOnClickListener(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_IS_VISIBLE, getSlidingPanel().isCollapsed())
    }

    private fun playAnimation() {
        binding.playPause.animationPlay(getSlidingPanel().isCollapsed())
    }

    private fun pauseAnimation() {
        binding.playPause.animationPause(getSlidingPanel().isCollapsed())
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (getSlidingPanel().isExpanded()) return

        if (toNext) {
            binding.next.playAnimation()
        } else {
            binding.previous.playAnimation()
        }
    }

    private fun updateProgressBarMax(max: Long) {
        binding.progressBar.max = max.toInt()
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            view?.alpha = MathUtils.clamp(1 - slideOffset * 3f, 0f, 1f)
            view?.toggleVisibility(slideOffset <= .8f, true)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.title.isSelected = newState == BottomSheetBehavior.STATE_COLLAPSED
        }
    }

}