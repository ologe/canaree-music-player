package dev.olog.presentation.offlinelyrics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
import dev.olog.presentation.interfaces.DrawsOnTop
import dev.olog.presentation.tutorial.TutorialTapTarget
import dev.olog.presentation.utils.removeLightStatusBar
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_offline_lyrics.*
import kotlinx.android.synthetic.main.fragment_offline_lyrics.view.*
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (viewModel.showAddLyricsIfNeverShown()) {
            TutorialTapTarget.addLyrics(view.search, view.edit, view.sync)
        }

        mediaProvider.observeMetadata()
            .subscribe(viewLifecycleOwner) {
                viewModel.updateCurrentTrackId(it.id)
                viewModel.updateCurrentMetadata(it.title, it.artist)
                viewLifecycleOwner.lifecycleScope.launch { loadImage(it.mediaId) }
                header.text = it.title
                subHeader.text = it.artist
                seekBar.max = it.duration.toInt()
                scrollView.scrollTo(0, 0)
            }


        mediaProvider.observePlaybackState()
            .subscribe(viewLifecycleOwner) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                viewModel.onStateChanged(it.bookmark, speed)
            }

        viewModel.observeLyrics()
            .subscribe(viewLifecycleOwner) { (lyrics, type) ->
                emptyState.toggleVisibility(lyrics.isEmpty(), true)
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

        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        edit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                EditLyricsDialog.show(act, viewModel.getLyrics()) { newLyrics ->
                    viewModel.updateLyrics(newLyrics)
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
        act.window.setLightStatusBar()

        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)
        seekBar.setOnSeekBarChangeListener(null)
        sync.setOnClickListener(null)
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
                image.setImageBitmap(original)
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


    override fun provideLayoutId(): Int = R.layout.fragment_offline_lyrics
}