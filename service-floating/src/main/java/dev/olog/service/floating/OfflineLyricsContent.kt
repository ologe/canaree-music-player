package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.core.schedulers.Schedulers
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.*
import dev.olog.shared.autoDisposeJob
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter,
    private val schedulers: Schedulers

) : Content() {

    private var lyricsJob by autoDisposeJob()

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private suspend fun loadImage(mediaId: MediaId) {
        try {
            val original = context.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
            val blurred = BlurKit.getInstance().blur(original, 20)
            withContext(schedulers.main){
                content.image.setImageBitmap(blurred)
            }
        } catch (ex: Exception){
            Timber.e(ex)
        }
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()

        presenter.onStart()

        glueService.observeMetadata()
            .onEach {
                presenter.updateCurrentTrackId(it.id)
                content.textWrapper.update(it.title, it.artist)
                content.seekBar.max = it.duration.toInt()
                loadImage(it.mediaId)

                if (presenter.firstEnter) {
                    presenter.firstEnter = false
                    content.list.scrollToCurrent()
                } else {
                    content.list.smoothScrollToPosition(0)
                }
            }.launchIn(lifecycleScope)

        presenter.observeLyrics()
            .onEach {
                content.list.adapter.suspendSubmitList(it.lines)
                content.list.awaitAnimationEnd()
                content.emptyState.isVisible = it.lines.isEmpty()
            }.launchIn(lifecycleScope)

        content.seekBar.observeProgress()
            .onEach { content.list.adapter.updateTime(it) }
            .launchIn(lifecycleScope)

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .onEach { content.seekBar.onStateChanged(it) }
            .launchIn(lifecycleScope)

        content.image.observePaletteColors()
            .map { it.accent }
            .onEach {
                content.edit.animateBackgroundColor(it)
                content.artist.animateTextColor(it)
            }.launchIn(lifecycleScope)

        content.list.onTap = {
            glueService.playPause()
        }

        content.edit.onClick {
            EditLyricsDialog.show(context, presenter.getLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }

        content.sync.onClick {
            try {
                OfflineLyricsSyncAdjustementDialog.show(
                    context,
                    presenter.getSyncAdjustment()
                ) {
                    presenter.updateSyncAdjustment(it)
                }
            } catch (ex: Exception){
                Timber.e(ex)
            }
        }

        content.fakeNext.setOnClickListener {
            content.list.adapter.debounceUpdate()
            glueService.skipToNext()
        }
        content.fakePrev.setOnClickListener {
            content.list.adapter.debounceUpdate()
            glueService.skipToPrevious()
        }

        content.seekBar.setListener(onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()
        presenter.onStop()
        content.edit.setOnClickListener(null)
        content.sync.setOnClickListener(null)
        content.fakeNext.setOnTouchListener(null)
        content.fakePrev.setOnTouchListener(null)
        content.seekBar.setOnSeekBarChangeListener(null)
    }

    override fun onDispose() {
        super.onDispose()
        lyricsJob = null
        presenter.dispose()
    }

}