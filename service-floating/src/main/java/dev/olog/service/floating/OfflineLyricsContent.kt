package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.asLiveData
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.*
import dev.olog.service.floating.api.Content
import dev.olog.service.floating.databinding.ContentOfflineLyricsBinding
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null

    private val content: ContentOfflineLyricsBinding = ContentOfflineLyricsBinding.inflate(LayoutInflater.from(context), null, false)

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(context) { glueService.playPause() } }

    private suspend fun loadImage(mediaId: MediaId) {
        try {
            val original = context.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                content.image.setImageBitmap(blurred)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

    override fun getView(): View = content.root

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()

        presenter.onStart()

        glueService.observePlaybackState()
            .subscribe(this) { content.seekBar.onStateChanged(it) }

        content.edit.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                EditLyricsDialog.show(context, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        content.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(this, {
                content.edit.animateBackgroundColor(it)
                content.subHeader.animateTextColor(it)
            })

        glueService.observeMetadata()
            .subscribe(this) {
                presenter.updateCurrentTrackId(it.id)
                GlobalScope.launch { loadImage(it.mediaId) }
                content.header.text = it.title
                content.subHeader.text = it.artist
                content.seekBar.max = it.duration.toInt()
                content.scrollView.scrollTo(0, 0)
            }

        content.sync.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    OfflineLyricsSyncAdjustementDialog.show(
                        context,
                        presenter.getSyncAdjustment()
                    ) {
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

        glueService.observePlaybackState()
            .subscribe(this) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }

        presenter.observeLyrics()
            .subscribe(this) { (lyrics, type) ->
                content.emptyState.toggleVisibility(lyrics.isEmpty(), true)
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
            }

        content.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
            presenter.resetTick()
        })
    }

    override fun onHidden() {
        super.onHidden()
        presenter.onStop()
        content.edit.setOnClickListener(null)
        content.sync.setOnClickListener(null)
        content.fakeNext.setOnTouchListener(null)
        content.fakePrev.setOnTouchListener(null)
        content.scrollView.setOnTouchListener(null)
        content.seekBar.setOnSeekBarChangeListener(null)

        lyricsJob?.cancel()
    }

}