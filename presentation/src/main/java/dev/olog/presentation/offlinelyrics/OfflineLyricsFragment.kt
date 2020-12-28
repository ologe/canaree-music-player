package dev.olog.presentation.offlinelyrics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.feature.base.base.BaseFragment
import dev.olog.lib.image.provider.OnImageLoadingError
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.lib.media.mediaProvider
import dev.olog.lib.offline.lyrics.*
import dev.olog.presentation.R
import dev.olog.feature.base.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.shared.widgets.extension.removeLightStatusBar
import dev.olog.shared.widgets.extension.setLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class OfflineLyricsFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter

    private val scrollViewTouchListener by lazyFast {
        NoScrollTouchListener(requireContext()) { requireActivity().mediaProvider.playPause() }
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
        if (presenter.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(search, edit, sync)
        }

        requireActivity().mediaProvider.metadata
            .onEach {
                presenter.updateCurrentTrackId(it.id)
                presenter.updateCurrentMetadata(it.title, it.artist)
                launch { loadImage(it.mediaId) }
                header.text = it.title
                subHeader.text = it.artist
                seekBar.max = it.duration.toLongMilliseconds().toInt()
                scrollView.scrollTo(0, 0)
            }.launchIn(this)


        requireActivity().mediaProvider.playbackState
            .onEach {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }.launchIn(this)

        presenter.observeLyrics()
            .onEach { (lyrics, type) ->
                emptyState.isVisible = lyrics.isEmpty()
                text.text = lyrics

                text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(text, lyrics, presenter.currentParagraph)
                        scrollView.scrollTo(0, scrollTo)
                    }
                }
            }.launchIn(this)

        requireActivity().mediaProvider.playbackState
            .filter { it.isPlayOrPause }
            .onEach { seekBar.onStateChanged(it) }
            .launchIn(this)

        image.observePaletteColors()
            .map { it.accent }
            .onEach { accent ->
                subHeader.animateTextColor(accent)
                edit.animateBackgroundColor(accent)
            }.launchIn(this)
    }

    override fun onStart() {
        super.onStart()

        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            launch {
                EditLyricsDialog.show(requireContext(), presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        search.setOnClickListener {
            searchLyrics()
        }
        requireActivity().window.removeLightStatusBar()

        fakeNext.setOnClickListener {
            requireActivity().mediaProvider.skipToNext()
        }
        fakePrev.setOnClickListener {
            requireActivity().mediaProvider.skipToPrevious()
        }
        scrollView.setOnTouchListener(scrollViewTouchListener)

        sync.setOnClickListener { _ ->
            launch {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(
                        requireContext(),
                        presenter.getSyncAdjustment()
                    ) {
                        presenter.updateSyncAdjustment(it)
                    }
                } catch (ex: Throwable){
                    ex.printStackTrace()
                }
            }
        }

        seekBar.setListener(onStopTouch = {
            requireActivity().mediaProvider.seekTo(seekBar.progress.toLong())
            presenter.resetTick()
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

    override fun onStop() {
        super.onStop()
        presenter.onStop()
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

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(requireContext(), customTabIntent, uri, callback)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}