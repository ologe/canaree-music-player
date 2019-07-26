package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import dev.olog.core.MediaId
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.offlinelyrics.NoScrollTouchListener
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.*
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private suspend fun loadImage(mediaId: MediaId) {
        withContext(Dispatchers.Main){
            GlideApp.with(context).clear(content.image)
        }
        try {
            val original = context.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(Dispatchers.Main){
                content.image.setImageBitmap(blurred)
            }
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()

        content.edit.setOnClickListener {
            EditLyricsDialog.showForService(context, presenter.getOriginalLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        content.sync.setOnClickListener {
            dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog.showForService(
                context,
                presenter.getSyncAdjustement()
            ) {
                presenter.updateSyncAdjustement(it)
            }
        }
        content.fakeNext.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToNext() })
        content.fakePrev.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToPrevious() })
        content.scrollView.setOnTouchListener(NoScrollTouchListener(context) { glueService.playPause() })

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

        glueService.observePlaybackState()
            .subscribe(this) { content.seekBar.onStateChanged(it) }

        lyricsJob = GlobalScope.launch {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(context, content.seekBar.progress, it) }
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

        lyricsJob?.cancel()
    }

}