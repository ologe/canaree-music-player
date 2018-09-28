package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Priority
import dev.olog.msc.R
import dev.olog.msc.app.GlideApp
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.floating.window.service.api.Content
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import dev.olog.msc.floating.window.service.music.service.MusicServiceMetadata
import dev.olog.msc.glide.transformation.BlurTransformation
import dev.olog.msc.offline.lyrics.EditLyricsDialog
import dev.olog.msc.offline.lyrics.NoScrollTouchListener
import dev.olog.msc.offline.lyrics.OfflineLyricsSyncAdjustementDialog
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.utils.blur.FastBlur
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

class OfflineLyricsContent(
        lifecycle: Lifecycle,
        private val context: Context,
        private val musicServiceBinder: MusicServiceBinder,
        private val presenter: OfflineLyricsContentPresenter

) : Content, DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()
    private var updateDisposable : Disposable? = null

    val content : View = LayoutInflater.from(context).inflate(R.layout.content_offline_lyrics, null)

    private val header = content.findViewById<TextView>(R.id.header)
    private val subHeader = content.findViewById<TextView>(R.id.subHeader)
    private val edit = content.findViewById<ImageButton>(R.id.edit)
    private val sync = content.findViewById<ImageButton>(R.id.sync)
    private val lyricsText = content.findViewById<TextView>(R.id.text)
    private val image = content.findViewById<ImageView>(R.id.image)
    private val emptyState = content.findViewById<TextView>(R.id.emptyState)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val fakeNext = content.findViewById<View>(R.id.fakeNext)
    private val fakePrev = content.findViewById<View>(R.id.fakePrev)
    private val scrollView = content.findViewById<ScrollView>(R.id.scrollBar)

    init {
        lifecycle.addObserver(this)

        musicServiceBinder.onMetadataChanged
                .subscribe({
                    presenter.updateCurrentTrackId(it.id)
                    loadImage(it, it.image)
                    header.text = it.title
                    subHeader.text = it.artist
                    updateProgressBarMax(it.duration)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.animatePlayPauseLiveData
                .subscribe({
                    handleSeekBarState(it == PlaybackStateCompat.STATE_PLAYING)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onBookmarkChangedLiveData
                .subscribe({ seekBar.progress = it.toInt() }, Throwable::printStackTrace)
                .addTo(subscriptions)

        presenter.observeLyrics()
                .map { presenter.transformLyrics(context, seekBar.progress, it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    emptyState.toggleVisibility(it.isEmpty(), true)
                    lyricsText.setText(it)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        setupSeekBar()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
        updateDisposable.unsubscribe()
    }

    private fun loadImage(metadata: MusicServiceMetadata, image: DisplayableItem){
        GlideApp.with(context).clear(this.image)

        val radius = 8
        val sampling = 6

        val drawable = CoverUtils.getGradient(context, MediaId.songId(metadata.id))
        val bitmap = drawable.toBitmap(100, 100, Bitmap.Config.RGB_565)
        val placeholder = FastBlur.blur(bitmap, radius, false)
                .toDrawable(content.resources)

        GlideApp.with(context)
                .load(image)
                .placeholder(placeholder)
                .priority(Priority.IMMEDIATE)
                .transform(BlurTransformation(radius, sampling))
                .override(500)
                .into(this.image)
    }

    override fun getView(): View = content

    override fun isFullscreen(): Boolean = true

    override fun onShown() {
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
        fakeNext.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.skipToNext() })
        fakePrev.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.skipToPrevious() })
        scrollView.setOnTouchListener(NoScrollTouchListener(context) { musicServiceBinder.playPause() })
    }

    override fun onHidden() {
        edit.setOnClickListener(null)
        sync.setOnClickListener(null)
        fakeNext.setOnTouchListener(null)
        fakePrev.setOnTouchListener(null)
        scrollView.setOnTouchListener(null)
    }

    private fun handleSeekBarState(isPlaying: Boolean){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar()
        }
    }

    private fun updateProgressBarMax(max: Long) {
        seekBar.max = max.toInt()
    }

    private fun resumeSeekBar(){
        updateDisposable = Observable.interval(AppConstants.PROGRESS_BAR_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
                .subscribe({ seekBar.incrementProgressBy(AppConstants.PROGRESS_BAR_INTERVAL) }, Throwable::printStackTrace)
    }

    private fun setupSeekBar(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                musicServiceBinder.seekTo(seekBar.progress.toLong())
            }
        })
    }

}