package dev.olog.feature.offline.lyrics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.media.MediaProvider
import dev.olog.feature.base.BaseFragment
import dev.olog.feature.base.DrawsOnTop
import dev.olog.shared.widgets.TutorialTapTarget
import dev.olog.shared.widgets.extension.removeLightStatusBar
import dev.olog.shared.widgets.extension.setLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saschpe.android.customtabs.CustomTabsHelper
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class OfflineLyricsFragment : BaseFragment(), DrawsOnTop {

    companion object {
        const val TAG = "OfflineLyricsFragment"

        @JvmStatic
        fun newInstance(): OfflineLyricsFragment {
            return OfflineLyricsFragment()
        }
    }

    @Inject
    lateinit var presenter: OfflineLyricsFragmentPresenter

    private val mediaProvider by lazy { requireContext().findInContext<MediaProvider>() }

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(ctx) { mediaProvider.playPause() } }

    private val callback = object : CustomTabsHelper.CustomTabFallback {
        override fun openUri(context: Context?, uri: Uri?) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (requireActivity().packageManager.isIntentSafe(intent)) {
                requireActivity().startActivity(intent)
            } else {
                requireActivity().toast(localization.R.string.common_browser_not_found)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (presenter.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(view.search, view.edit, view.sync)
        }

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                presenter.updateCurrentTrackId(it.id)
                presenter.updateCurrentMetadata(it.title, it.artist)
                viewLifecycleOwner.lifecycleScope.launch { loadImage(it.mediaId) }
                header.text = it.title
                subHeader.text = it.artist
                seekBar.max = it.duration.toInt()
                scrollView.scrollTo(0, 0)
            }


        mediaProvider.observePlaybackState()
            .subscribe(viewLifecycleOwner) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }

        presenter.observeLyrics()
            .subscribe(viewLifecycleOwner) { (lyrics, type) ->
                emptyState.toggleVisibility(lyrics.isEmpty(), true)
                text.text = lyrics

                text.doOnPreDraw {
                    if (type is LyricsMode.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(text, lyrics, presenter.currentParagraph)
                        scrollView.scrollTo(0, scrollTo)
                    }
                }
            }

        mediaProvider.observePlaybackState()
            .filter { it.isPlayOrPause }
            .subscribe(viewLifecycleOwner) { seekBar.onStateChanged(it) }

        view.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(viewLifecycleOwner) { accent ->
                subHeader.animateTextColor(accent)
                edit.animateBackgroundColor(accent)
            }
    }

    override fun onStart() {
        super.onStart()

        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                EditLyricsDialog.show(act, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }
        back.setOnClickListener { act.onBackPressed() }
        search.setOnClickListener { searchLyrics() }
        act.window.removeLightStatusBar()

        fakeNext.setOnClickListener { mediaProvider.skipToNext() }
        fakePrev.setOnClickListener { mediaProvider.skipToPrevious() }
        scrollView.setOnTouchListener(scrollViewTouchListener)

        sync.setOnClickListener { _ ->
            viewLifecycleOwner.lifecycleScope.launch {
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

        seekBar.setListener(onStopTouch = {
            mediaProvider.seekTo(seekBar.progress.toLong())
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
        act.window.setLightStatusBar()

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
            val blurred = BlurKit.getInstance().blur(original, 20) // TODO inject?
            withContext(Dispatchers.Main){
                image.setImageBitmap(blurred)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

    private fun searchLyrics() {
        val customTabIntent = CustomTabsIntent.Builder() // TODO wrap into a navigator, migrato to builtin api?
            .enableUrlBarHiding()
            .setToolbarColor(ctx.colorSurface())
            .build()
        CustomTabsHelper.addKeepAliveExtra(ctx, customTabIntent.intent)

        val escapedQuery = URLEncoder.encode(presenter.getInfoMetadata(), "UTF-8")
        val uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        CustomTabsHelper.openCustomTab(ctx, customTabIntent, uri, callback)
    }


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}