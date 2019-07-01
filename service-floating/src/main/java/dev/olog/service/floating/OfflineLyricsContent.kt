package dev.olog.service.floating

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.Priority
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.media.*
import dev.olog.offlinelyrics.EditLyricsDialog
import dev.olog.service.floating.api.Content
import dev.olog.shared.extensions.*
import dev.olog.shared.flowInterval
import dev.olog.shared.widgets.BlurImageView
import dev.olog.shared.widgets.progressbar.CustomSeekBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.rx2.collect
import java.util.concurrent.TimeUnit

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null
    private var paletteDisposable: Disposable? = null

    val content: View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val header = content.findViewById<TextView>(R.id.header)
    private val subHeader = content.findViewById<TextView>(R.id.subHeader)
    private val edit = content.findViewById<FloatingActionButton>(R.id.edit)
    private val sync = content.findViewById<ImageButton>(R.id.sync)
    private val lyricsText = content.findViewById<TextView>(R.id.text)
    private val image = content.findViewById<BlurImageView>(R.id.image)
    private val emptyState = content.findViewById<TextView>(R.id.emptyState)
    private val seekBar = content.findViewById<CustomSeekBar>(R.id.seekBar)
    private val fakeNext = content.findViewById<View>(R.id.fakeNext)
    private val fakePrev = content.findViewById<View>(R.id.fakePrev)
    private val scrollView = content.findViewById<ScrollView>(R.id.scrollBar)


    private fun loadImage(metadata: MediaMetadataCompat) {
        GlideApp.with(context).clear(this.image)

        val drawable = CoverUtils.getGradient(context, metadata.getMediaId())

        GlideApp.with(context)
            .load(metadata.getMediaId())
            .placeholder(drawable)
            .priority(Priority.IMMEDIATE)
            .override(500)
            .into(this.image)
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
        super.onShown()
        edit.setOnClickListener {
            EditLyricsDialog.showForService(context, presenter.getOriginalLyrics()) { newLyrics ->
                presenter.updateLyrics(newLyrics)
            }
        }
        sync.setOnClickListener {
            dev.olog.offlinelyrics.OfflineLyricsSyncAdjustementDialog.showForService(
                context,
                presenter.getSyncAdjustement()
            ) {
                presenter.updateSyncAdjustement(it)
            }
        }
        fakeNext.setOnTouchListener(dev.olog.offlinelyrics.NoScrollTouchListener(context) { glueService.skipToNext() })
        fakePrev.setOnTouchListener(dev.olog.offlinelyrics.NoScrollTouchListener(context) { glueService.skipToPrevious() })
        scrollView.setOnTouchListener(dev.olog.offlinelyrics.NoScrollTouchListener(context) { glueService.playPause() })

        paletteDisposable = image.observePaletteColors()
            .map { it.accent }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                edit.animateBackgroundColor(it)
                subHeader.animateTextColor(it)
            }, Throwable::printStackTrace)

        glueService.observeMetadata()
            .subscribe(this) {
                presenter.updateCurrentTrackId(it.getId())
                loadImage(it)
                header.text = it.getTitle()
                subHeader.text = it.getArtist()
                seekBar.max = it.getDuration().toInt()
            }

        glueService.observePlaybackState()
            .subscribe(this) { seekBar.onStateChanged(it) }

        lyricsJob = GlobalScope.launch {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(context, seekBar.progress, it) }
                .collect {
                    withContext(Dispatchers.Main) {
                        emptyState.toggleVisibility(it.isEmpty(), true)
                        lyricsText.text = it
                    }
                }
        }

        seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()

        paletteDisposable.unsubscribe()
        edit.setOnClickListener(null)
        sync.setOnClickListener(null)
        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)

        lyricsJob?.cancel()
    }

}