package dev.olog.presentation.offlinelyrics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.media.MediaProvider
import dev.olog.offlinelyrics.*
import dev.olog.presentation.R
import dev.olog.presentation.databinding.FragmentOfflineLyricsBinding
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class OfflineLyricsFragment : Fragment(R.layout.fragment_offline_lyrics), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter
    private val binding by viewBinding(FragmentOfflineLyricsBinding::bind)

    private val mediaProvider by lazy { activity!!.findInContext<MediaProvider>() }

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(ctx) { mediaProvider.playPause() } }

    private val callback = object : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (requireActivity().packageManager.isIntentSafe(intent)) {
                requireActivity().startActivity(intent)
            } else {
                requireActivity().toast(R.string.common_browser_not_found)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (presenter.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(binding.search, binding.edit, binding.sync)
        }

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                presenter.updateCurrentTrackId(it.id)
                presenter.updateCurrentMetadata(it.title, it.artist)
                viewLifecycleScope.launch { loadImage(it.mediaId) }
                binding.header.text = it.title
                binding.subHeader.text = it.artist
                binding.seekBar.max = it.duration.toInt()
                binding.scrollView.scrollTo(0, 0)
            }


        mediaProvider.observePlaybackState()
            .subscribe(viewLifecycleOwner) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }

        presenter.observeLyrics()
            .subscribe(viewLifecycleOwner) { (lyrics, type) ->
                binding.emptyState.toggleVisibility(lyrics.isEmpty(), true)
                binding.text.text = lyrics

                binding.text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(binding.text, lyrics, presenter.currentParagraph)
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

        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.edit.setOnClickListener {
            viewLifecycleScope.launch {
                EditLyricsDialog.show(act, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
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
            viewLifecycleScope.launch {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(
                        ctx,
                        presenter.getSyncAdjustment()
                    ) {
                        presenter.updateSyncAdjustment(it)
                    }
                } catch (ex: Throwable){
                    ex.printStackTrace()
                }
            }
        }

        binding.seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(binding.seekBar.progress.toLong())
            presenter.resetTick()
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
        presenter.onStop()
    }

    private suspend fun loadImage(mediaId: MediaId) = withContext(Dispatchers.IO){
        try {
            val original = requireContext().getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                binding.image.setImageBitmap(blurred)
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
        CustomTabsHelper.addKeepAliveExtra(ctx, customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(ctx, customTabIntent, uri, callback)
    }

}