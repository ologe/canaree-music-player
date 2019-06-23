package dev.olog.msc.floating.window.service

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
import dev.olog.msc.R
import dev.olog.msc.floating.window.service.api.Content
import dev.olog.msc.floating.window.service.music.service.MusicGlueService
import dev.olog.msc.offline.lyrics.EditLyricsDialog
import dev.olog.msc.offline.lyrics.NoScrollTouchListener
import dev.olog.msc.offline.lyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.msc.presentation.widget.animateBackgroundColor
import dev.olog.msc.presentation.widget.animateTextColor
import dev.olog.msc.presentation.widget.image.view.BlurImageView
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.flowInterval
import dev.olog.shared.toggleVisibility
import dev.olog.shared.unsubscribe
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

    private var seekBarJob: Job? = null
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
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
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
            OfflineLyricsSyncAdjustementDialog.showForService(context, presenter.getSyncAdjustement()) {
                presenter.updateSyncAdjustement(it)
            }
        }
        fakeNext.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(context) { glueService.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(context) { glueService.playPause() })

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
                updateProgressBarMax(it.getDuration())
            }

        glueService.observePlaybackState()
            .subscribe(this) {
                handleSeekBarState(it.isPlaying(), it.playbackSpeed)
            }

        glueService.observePlaybackState()
            .subscribe(this) {
                handleSeekBarState(it.isPlaying(), it.playbackSpeed)
            }

        lyricsJob = GlobalScope.launch {
            presenter.observeLyrics()
                .map { presenter.transformLyrics(context, seekBar.progress, it) }
                .collect {
                    withContext(Dispatchers.Main) {
                        emptyState.toggleVisibility(it.isEmpty(), true)
                        lyricsText.setText(it)
                    }
                }
        }

        setupSeekBar()
    }

    override fun onHidden() {
        super.onHidden()

        paletteDisposable.unsubscribe()
        edit.setOnClickListener(null)
        sync.setOnClickListener(null)
        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)

        seekBarJob?.cancel()
        lyricsJob?.cancel()
    }

    private fun handleSeekBarState(isPlaying: Boolean, speed: Float) {
        seekBarJob?.cancel()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun updateProgressBarMax(max: Long) {
        seekBar.max = max.toInt()
    }

    private fun resumeSeekBar(speed: Float) {
        seekBarJob = GlobalScope.launch(Dispatchers.Main) {
            flowInterval(
                250,
                TimeUnit.MILLISECONDS
            ).collect { seekBar.incrementProgressBy((250 * speed).toInt()) }
        }
    }

    private fun setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                glueService.seekTo(seekBar.progress.toLong())
            }
        })
    }

}