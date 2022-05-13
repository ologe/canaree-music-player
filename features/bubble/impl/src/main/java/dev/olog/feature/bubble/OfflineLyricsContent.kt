package dev.olog.feature.bubble

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.feature.bubble.api.Content
import dev.olog.feature.lyrics.offline.FeatureLyricsOfflineNavigator
import dev.olog.feature.lyrics.offline.Lyrics
import dev.olog.feature.lyrics.offline.NoScrollTouchListener
import dev.olog.feature.lyrics.offline.LyricsOfflinePresenter
import dev.olog.feature.lyrics.offline.OffsetCalculator
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.shared.extension.animateBackgroundColor
import dev.olog.shared.extension.animateTextColor
import dev.olog.shared.extension.collectOnLifecycle
import dev.olog.shared.extension.lazyFast
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: LyricsOfflinePresenter,
    private val featureLyricsOfflineNavigator: FeatureLyricsOfflineNavigator,
) : Content() {

    private var lyricsJob: Job? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(context) { glueService.playPause() } }

    private suspend fun loadImage(mediaId: MediaId) = withContext(Dispatchers.IO) {
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

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()

        presenter.onStart()

        glueService.observePlaybackState()
            .collectOnLifecycle(this) { content.seekBar.onStateChanged(it) }

        content.edit.setOnClickListener {
            lifecycleScope.launch {
                featureLyricsOfflineNavigator.toEditDialog(context, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        content.image.observePaletteColors()
            .map { it.accent }
            .collectOnLifecycle(this) {
                content.edit.animateBackgroundColor(it)
                content.subHeader.animateTextColor(it)
            }

        glueService.observeMetadata()
            .collectOnLifecycle(this) {
                presenter.updateCurrentTrackId(it.id)
                loadImage(it.mediaId)
                content.header.text = it.title
                content.subHeader.text = it.artist
                content.seekBar.max = it.duration.toInt()
                content.scrollView.scrollTo(0, 0)
            }

        content.sync.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    featureLyricsOfflineNavigator.toSyncAdjustment(
                        context = context,
                        currentSync = presenter.getSyncAdjustment()
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
            .collectOnLifecycle(this) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }

        presenter.observeLyrics()
            .collectOnLifecycle(this) { (lyrics, type) ->
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