package dev.olog.service.floating

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.olog.core.mediaid.MediaId
import dev.olog.lib.image.provider.OnImageLoadingError
import dev.olog.lib.image.provider.getCachedBitmap
import dev.olog.lib.offline.lyrics.*
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.animateBackgroundColor
import dev.olog.shared.android.extensions.animateTextColor
import dev.olog.shared.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineLyricsContent(
    private val service: LifecycleService,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter
) : Content {

    val content: View = LayoutInflater.from(service).inflate(R.layout.content_offline_lyrics, null)

    private val scrollViewTouchListener by lazyFast {
        NoScrollTouchListener(service) { glueService.playPause() }
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    init {
        glueService.playbackState
            .onEach { content.seekBar.onStateChanged(it) }
            .launchIn(service.lifecycleScope)

        content.image.observePaletteColors()
            .map { it.accent }
            .onEach {
                content.edit.animateBackgroundColor(it)
                content.subHeader.animateTextColor(it)
            }.launchIn(service.lifecycleScope)


        glueService.metadata
            .onEach {
                presenter.updateCurrentTrackId(it.id)
                service.lifecycleScope.launch { loadImage(it.mediaId) }
                content.header.text = it.title
                content.subHeader.text = it.artist
                content.seekBar.max = it.duration.toLongMilliseconds().toInt()
                content.scrollView.scrollTo(0, 0)
            }.launchIn(service.lifecycleScope)

        glueService.playbackState
            .onEach {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }.launchIn(service.lifecycleScope)

        presenter.observeLyrics()
            .onEach { (lyrics, type) ->
                content.emptyState.isVisible = lyrics.isEmpty()
                content.text.text = lyrics

                content.text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(content.text, lyrics, presenter.currentParagraph)
                        content.scrollView.smoothScrollTo(0, scrollTo)
                    }
                }

                if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                    val scrollTo = OffsetCalculator.compute(content.text, lyrics, presenter.currentParagraph)
                    content.scrollView.smoothScrollTo(0, scrollTo)
                }
            }.launchIn(service.lifecycleScope)
    }

    override fun onShown() {
        presenter.onStart()

        content.edit.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                EditLyricsDialog.show(service, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        content.sync.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(service, presenter.getSyncAdjustment()) {
                        presenter.updateSyncAdjustment(it)
                    }
                } catch (ex: Throwable){
                    ex.printStackTrace()
                }
            }
        }
        content.fakeNext.setOnClickListener { glueService.skipToNext() }
        content.fakePrev.setOnClickListener { glueService.skipToPrevious() }
        content.scrollView.setOnTouchListener(scrollViewTouchListener)

        content.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
            presenter.resetTick()
        })
    }

    override fun onHidden() {
        presenter.onStop()
        content.edit.setOnClickListener(null)
        content.sync.setOnClickListener(null)
        content.fakeNext.setOnTouchListener(null)
        content.fakePrev.setOnTouchListener(null)
        content.scrollView.setOnTouchListener(null)
        content.seekBar.setOnSeekBarChangeListener(null)
    }

    private suspend fun loadImage(mediaId: MediaId) {
        try {
            val original = service.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                content.image.setImageBitmap(blurred)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

}