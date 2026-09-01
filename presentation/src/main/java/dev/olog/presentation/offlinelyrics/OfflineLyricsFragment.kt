package dev.olog.presentation.offlinelyrics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.media.MediaProvider
import dev.olog.offlinelyrics.*
import dev.olog.presentation.R
import dev.olog.presentation.base.BaseFragment
import dev.olog.presentation.databinding.FragmentOfflineLyricsBinding
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OfflineLyricsFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    private val viewModel by viewModels<OfflineLyricsFragmentViewModel>()

    private val mediaProvider by lazy { activity!!.asType<MediaProvider>() }

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(ctx) { mediaProvider.playPause() } }

    private var _binding: FragmentOfflineLyricsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineLyricsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(binding.search, binding.edit, binding.sync)
        }

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                viewModel.updateCurrentTrackId(it.id)
                viewModel.updateCurrentMetadata(it.title, it.artist)
                viewLifecycleOwner.lifecycleScope.launch { loadImage(it.mediaId) }
                binding.header.text = it.title
                binding.subHeader.text = it.artist
                binding.seekBar.max = it.duration.toInt()
                binding.scrollView.scrollTo(0, 0)
            }


        mediaProvider.observePlaybackState()
            .subscribe(viewLifecycleOwner) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                viewModel.onStateChanged(it.bookmark, speed)
            }

        viewModel.observeLyrics()
            .subscribe(viewLifecycleOwner) { (lyrics, type) ->
                binding.emptyState.toggleVisibility(lyrics.isEmpty(), true)
                binding.text.text = lyrics

                binding.text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(binding.text, lyrics, viewModel.currentParagraph)
                        binding.scrollView.scrollTo(0, scrollTo)
                    }
                }
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .subscribe(viewLifecycleOwner) { binding.seekBar.onStateChanged(it) }

        binding.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewLifecycleOwner) { accent ->
                binding.subHeader.animateTextColor(accent)
                binding.edit.animateBackgroundColor(accent)
            }
    }

    override fun onStart() {
        super.onStart()

        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.edit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                EditLyricsDialog.show(act, viewModel.getLyrics()) { newLyrics ->
                    viewModel.updateLyrics(newLyrics)
                }
            }
        }
        binding.back.setOnClickListener { act.onBackPressed() }
        binding.search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        binding.fakeNext.setOnClickListener { mediaProvider.skipToNext() }
        binding.fakePrev.setOnClickListener { mediaProvider.skipToPrevious() }
        binding.scrollView.setOnTouchListener(scrollViewTouchListener)

        binding.sync.setOnClickListener { _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(
                        ctx,
                        viewModel.getSyncAdjustment()
                    ) {
                        viewModel.updateSyncAdjustment(it)
                    }
                } catch (ex: Throwable){
                    ex.printStackTrace()
                }
            }
        }

        binding.seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(binding.seekBar.progress.toLong())
            viewModel.resetTick()
        }, onStartTouch = {
        }, onProgressChanged = {
        })
    }

    override fun onPause() {
        super.onPause()
        binding.edit.setOnClickListener(null)
        binding.back.setOnClickListener(null)
        binding.search.setOnClickListener(null)
        act.window.setLightStatusBar()

        binding.fakeNext.setOnTouchListener(null)
        binding.fakePrev.setOnTouchListener(null)
        binding.scrollView.setOnTouchListener(null)
        binding.seekBar.setOnSeekBarChangeListener(null)
        binding.sync.setOnClickListener(null)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private suspend fun loadImage(mediaId: MediaId) = withContext(Dispatchers.IO){
        try {
            val original = requireContext().getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
//            val blurred = BlurKit.getInstance().blur(original, 20) todo
            withContext(Dispatchers.Main){
                binding.image.setImageBitmap(original)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

    private fun searchLyrics() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(ctx.colorSurface())
            .build()

        val escapedQuery = URLEncoder.encode(viewModel.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        ctx.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }


}