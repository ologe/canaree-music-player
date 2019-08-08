package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.offlinelyrics.NoScrollTouchListener
import dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.*
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

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

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()

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
            }

        content.sync.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                OfflineLyricsSyncAdjustementDialog.show(
                    context,
                    presenter.getSyncAdjustment()
                ) {
                    presenter.updateSyncAdjustment(it)
                }
            }
        }
        content.fakeNext.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToNext() })
        content.fakePrev.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToPrevious() })
        content.scrollView.setOnTouchListener(NoScrollTouchListener(context) { glueService.playPause() })

        lyricsJob = GlobalScope.launch(Dispatchers.Main) {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(context, content.seekBar.progress, it) }
                .flowOn(Dispatchers.IO)
                .collect {
                    content.emptyState.toggleVisibility(it.isEmpty(), true)
                    content.text.text = it
                }
        }

        content.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()
        content.edit.setOnClickListener(null)
        content.sync.setOnClickListener(null)
        content.fakeNext.setOnTouchListener(null)
        content.fakePrev.setOnTouchListener(null)
        content.scrollView.setOnTouchListener(null)
        content.seekBar.setOnSeekBarChangeListener(null)

        lyricsJob?.cancel()
    }

}