package dev.olog.service.floating

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnPreDraw
import dev.olog.core.MediaId
import dev.olog.image.provider.OnImageLoadingError
import dev.olog.image.provider.getCachedBitmap
import dev.olog.offlinelyrics.*
import dev.olog.service.floating.api.Content
import dev.olog.service.floating.databinding.ContentOfflineLyricsBinding
import dev.olog.shared.android.extensions.*
import dev.olog.shared.lazyFast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import java.lang.Exception

class OfflineLyricsContent(
    private val context: Context,
    private val glueService: MusicGlueService,
    private val presenter: OfflineLyricsContentPresenter

) : Content() {

    private var lyricsJob: Job? = null

    private val binding = ContentOfflineLyricsBinding.inflate(LayoutInflater.from(context))

    val content: View = binding.root

    private val scrollViewTouchListener by lazyFast { NoScrollTouchListener(context) { glueService.playPause() } }

    private suspend fun loadImage(mediaId: MediaId) {
        try {
            val original = context.getCachedBitmap(mediaId, 300, onError = OnImageLoadingError.Placeholder(true))
//            val blurred = BlurKit.getInstance().blur(original, 20) todo
            withContext(Dispatchers.Main){
                binding.image.setImageBitmap(original)
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
            .subscribe(this) { binding.seekBar.onStateChanged(it) }

        binding.edit.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                EditLyricsDialog.show(context, presenter.getLyrics()) { newLyrics ->
                    presenter.updateLyrics(newLyrics)
                }
            }
        }

        binding.image.observePaletteColors()
            .map { it.accent }
            .asLiveData()
            .subscribe(this, {
                binding.edit.animateBackgroundColor(it)
                binding.subHeader.animateTextColor(it)
            })

        glueService.observeMetadata()
            .subscribe(this) {
                presenter.updateCurrentTrackId(it.id)
                GlobalScope.launch { loadImage(it.mediaId) }
                binding.header.text = it.title
                binding.subHeader.text = it.artist
                binding.seekBar.max = it.duration.toInt()
                binding.scrollView.scrollTo(0, 0)
            }

        binding.sync.setOnClickListener {
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
        binding.fakeNext.setOnClickListener { glueService.skipToNext() }
        binding.fakePrev.setOnClickListener { glueService.skipToPrevious() }
        binding.scrollView.setOnTouchListener(scrollViewTouchListener)

        glueService.observePlaybackState()
            .subscribe(this) {
                val speed = if (it.isPaused) 0f else it.playbackSpeed
                presenter.onStateChanged(it.bookmark, speed)
            }

        presenter.observeLyrics()
            .subscribe(this) { (lyrics, type) ->
                binding.emptyState.toggleVisibility(lyrics.isEmpty(), true)
                binding.text.text = lyrics

                binding.text.doOnPreDraw {
                    if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                        val scrollTo = OffsetCalculator.compute(binding.text, lyrics, presenter.currentParagraph)
                        binding.scrollView.smoothScrollTo(0, scrollTo)
                    }
                }

                if (type is Lyrics.Synced && !scrollViewTouchListener.userHasControl){
                    val scrollTo = OffsetCalculator.compute(binding.text, lyrics, presenter.currentParagraph)
                    binding.scrollView.smoothScrollTo(0, scrollTo)
                }
            }

        binding.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(binding.seekBar.progress.toLong())
            presenter.resetTick()
        })
    }

    override fun onHidden() {
        super.onHidden()
        presenter.onStop()
        binding.edit.setOnClickListener(null)
        binding.sync.setOnClickListener(null)
        binding.fakeNext.setOnTouchListener(null)
        binding.fakePrev.setOnTouchListener(null)
        binding.scrollView.setOnTouchListener(null)
        binding.seekBar.setOnSeekBarChangeListener(null)

        lyricsJob?.cancel()
    }

}