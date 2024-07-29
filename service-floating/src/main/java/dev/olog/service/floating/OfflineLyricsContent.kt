package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(context) { glueService.playPause() } }

    private suspend fun loadImage(
        mediaId: MediaId,
        image: ImageView,
    ) {
        try {
            val original = context.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                image.setImageBitmap(blurred)
            }
        } catch (ex: Throwable){
            ex.printStackTrace()
        }
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()
        val binging = ContentOfflineLyricsBinding.bind(content)

        presenter.onStart()

        glueService.observePlaybackState()
            .filterNotNull()
            .onEach { binging.seekBar.onStateChanged(it) }
            .launchIn(lifecycleScope)

        binging.edit.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                EditLyricsDialog.show(context, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        binging.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(this, {
                binging.edit.animateBackgroundColor(it)
                binging.subHeader.animateTextColor(it)
            })

        glueService.observeMetadata()
            .filterNotNull()
            .onEach {
                presenter.updateCurrentTrackId(it.id)
                GlobalScope.launch { loadImage(it.mediaId, binging.image) }
                binging.header.text = it.title
                binging.subHeader.text = it.artist
                binging.seekBar.max = it.duration.toInt()
                binging.scrollView.scrollTo(0, 0)
            }.launchIn(lifecycleScope)

        binging.sync.setOnClickListener {
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
        binging.fakeNext.setOnClickListener { glueService.skipToNext() }
        binging.fakePrev.setOnClickListener { glueService.skipToPrevious() }
        binging.scrollView.setOnTouchListener(scrollViewTouchListener)

        glueService.observePlaybackState()
            .filterNotNull()
            .onEach {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }.launchIn(lifecycleScope)

        presenter.observeLyrics()
            .subscribe(this) { (lyrics, type) ->
                binging.emptyState.toggleVisibility(lyrics.isEmpty(), true)
                binging.text.text = lyrics

                binging.text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(binging.text, lyrics, presenter.currentParagraph)
                        binging.scrollView.smoothScrollTo(0, scrollTo)
                    }
                }

                if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                    val scrollTo = OffsetCalculator.compute(binging.text, lyrics, presenter.currentParagraph)
                    binging.scrollView.smoothScrollTo(0, scrollTo)
                }
            }

        binging.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(binging.seekBar.progress.toLong())
            presenter.resetTick()
        })
    }

    override fun onHidden() {
        super.onHidden()
        presenter.onStop()

        val binging = ContentOfflineLyricsBinding.bind(content)
        binging.edit.setOnClickListener(null)
        binging.sync.setOnClickListener(null)
        binging.fakeNext.setOnTouchListener(null)
        binging.fakePrev.setOnTouchListener(null)
        binging.scrollView.setOnTouchListener(null)
        binging.seekBar.setOnSeekBarChangeListener(null)

        lyricsJob?.cancel()
    }

}