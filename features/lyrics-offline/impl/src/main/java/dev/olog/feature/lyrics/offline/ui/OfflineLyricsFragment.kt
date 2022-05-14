package dev.olog.feature.lyrics.offline.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.lyrics.offline.api.Lyrics
import dev.olog.feature.lyrics.offline.LyricsOfflineTutorial
import dev.olog.feature.lyrics.offline.api.NoScrollTouchListener
import dev.olog.feature.lyrics.offline.api.OffsetCalculator
import dev.olog.feature.lyrics.offline.R
import dev.olog.feature.lyrics.offline.base.EditLyricsDialog
import dev.olog.feature.lyrics.offline.base.OfflineLyricsSyncAdjustementDialog
import dev.olog.feature.media.api.MediaProvider
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.platform.DrawsOnTop
import dev.olog.platform.fragment.BaseFragment
import dev.olog.shared.extension.animateBackgroundColor
import dev.olog.shared.extension.animateTextColor
import dev.olog.shared.extension.collectOnLifecycle
import dev.olog.shared.extension.collectOnViewLifecycle
import dev.olog.shared.extension.findInContext
import dev.olog.shared.extension.isIntentSafe
import dev.olog.shared.extension.launchWhenResumed
import dev.olog.shared.extension.lazyFast
import dev.olog.shared.extension.toast
import dev.olog.ui.activity.removeLightStatusBar
import dev.olog.ui.activity.setLightStatusBar
import dev.olog.ui.colorSurface
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder

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

    private val mediaProvider: MediaProvider
        get() = requireContext().findInContext()

    private val scrollViewTouchListener by lazyFast {
        NoScrollTouchListener(requireContext()) { mediaProvider.playPause() }
    }

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
        if (viewModel.showAddLyricsIfNeverShown()) {
            LyricsOfflineTutorial.addLyrics(view.search, view.edit, view.sync)
        }

        mediaProvider.observeMetadata()
            .collectOnViewLifecycle(this) {
                viewModel.updateCurrentTrackId(it.id)
                viewModel.updateCurrentMetadata(it.title, it.artist)
                loadImage(it.mediaId)
                header.text = it.title
                subHeader.text = it.artist
                seekBar.max = it.duration.toInt()
                scrollView.scrollTo(0, 0)
            }


        mediaProvider.observePlaybackState()
            .collectOnViewLifecycle(this) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                viewModel.onStateChanged(it.bookmark, speed)
            }

        viewModel.observeLyrics()
            .collectOnLifecycle(this) { (lyrics, type) ->
                emptyState.isVisible = lyrics.isEmpty()
                text.text = lyrics

                text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(text, lyrics, viewModel.currentParagraph)
                        scrollView.scrollTo(0, scrollTo)
                    }
                }
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .collectOnViewLifecycle(this) { seekBar.onStateChanged(it) }

        view.image.observePaletteColors()
            .map { it.accent }
            .collectOnViewLifecycle(this) { accent ->
                subHeader.animateTextColor(accent)
                edit.animateBackgroundColor(accent)
            }
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            launchWhenResumed {
                EditLyricsDialog.show(requireContext(), viewModel.getLyrics()) { newLyrics ->
                    viewModel.updateLyrics(newLyrics)
                }
            }
        }
        back.setOnClickListener { requireActivity().onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        requireActivity().window.removeLightStatusBar()

        fakeNext.setOnClickListener { mediaProvider.skipToNext() }
        fakePrev.setOnClickListener { mediaProvider.skipToPrevious() }
        scrollView.setOnTouchListener(scrollViewTouchListener)

        sync.setOnClickListener { _ ->
            launchWhenResumed {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(
                        requireContext(),
                        viewModel.getSyncAdjustment()
                    ) {
                        viewModel.updateSyncAdjustment(it)
                    }
                } catch (ex: Throwable){
                    ex.printStackTrace()
                }
            }
        }

        seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(seekBar.progress.toLong())
            viewModel.resetTick()
        }, onStartTouch = {
        }, onProgressChanged = {
        })
    }

    override fun onPause() {
        super.onPause()
        edit.setOnClickListener(null)
        back.setOnClickListener(null)
        search.setOnClickListener(null)
        requireActivity().window.setLightStatusBar()

        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)
        seekBar.setOnSeekBarChangeListener(null)
        sync.setOnClickListener(null)
    }

    private suspend fun loadImage(mediaId: MediaId) = withContext(Dispatchers.IO){
        try {
            val original = requireContext().getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                image.setImageBitmap(blurred)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

    private fun searchLyrics() {
        val customTabIntent = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(requireContext().colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(requireContext(), customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(viewModel.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(requireContext(), customTabIntent, uri, callback)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}