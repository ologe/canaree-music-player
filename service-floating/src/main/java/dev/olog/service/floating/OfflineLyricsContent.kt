package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import dev.olog.core.MediaId
import dev.olog.core.schedulers.Schedulers
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.service.floating.api.Content
import dev.olog.shared.android.extensions.animateBackgroundColor
import dev.olog.shared.android.extensions.animateTextColor
import dev.olog.shared.android.extensions.subscribe
import dev.olog.shared.autoDisposeJob
import io.alterac.blurkit.BlurKit
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber

// TODO cancel presenter scope
class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter,
    private val schedulers: Schedulers

) : Content() {

    private var lyricsJob by autoDisposeJob()

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    init {
        content.list.onTap = {
            glueService.playPause()
        }

        glueService.observeMetadata()
            .onEach {
                presenter.updateCurrentTrackId(it.id)
                content.header.text = it.title
                content.subHeader.text = it.artist
                content.seekBar.max = it.duration.toInt()
                content.list.smoothScrollToPosition(0)

                loadImage(it.mediaId)
            }.launchIn(lifecycleScope)

        glueService.observePlaybackState()
            .onEach {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.isPlaying, it.bookmark, speed)
            }.launchIn(lifecycleScope)

        presenter.observeLyrics()
            .onEach {
                content.emptyState.isVisible = it.lines.isEmpty()
                content.list.adapter.submitList(it.lines)
            }.launchIn(lifecycleScope)

        presenter.observeCurrentProgress
            .subscribe(this) { time ->
                content.list.adapter.updateTime(time)
            }

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .onEach { content.seekBar.onStateChanged(it) }
            .launchIn(lifecycleScope)

        content.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(this) {
                content.edit.animateBackgroundColor(it)
                content.subHeader.animateTextColor(it)
            }
    }

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

        content.edit.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                EditLyricsDialog.show(context, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        content.sync.setOnClickListener {
            lifecycleScope.launchWhenResumed {
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
        }
        content.fakeNext.setOnClickListener { glueService.skipToNext() }
        content.fakePrev.setOnClickListener { glueService.skipToPrevious() }

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

        lyricsJob = null
    }

}