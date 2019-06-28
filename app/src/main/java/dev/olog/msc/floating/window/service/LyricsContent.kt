package dev.olog.msc.floating.window.service

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.media.getArtist
import dev.olog.media.getTitle
import dev.olog.media.isPlaying
import dev.olog.msc.R
import dev.olog.shared.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.floating.window.service.music.service.MusicGlueService
import dev.olog.shared.widgets.playpause.IPlayPauseBehavior
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.extensions.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class LyricsContent (
        lifecycle: Lifecycle,
        context: Context,
        private val musicServiceBinder: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player), DefaultLifecycleObserver {

    private val playPauseBehavior = content.findViewById<ImageButton>(R.id.playPause) as IPlayPauseBehavior
    private val playPause = content.findViewById<ImageButton>(R.id.playPause)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val title = content.findViewById<TextView>(R.id.header)
    private val artist = content.findViewById<TextView>(R.id.subHeader)

    private val subscriptions = CompositeDisposable()
    private var updateDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        playPause.setOnClickListener { musicServiceBinder.playPause() }

        musicServiceBinder.observePlaybackState()
                .subscribe(this) {
                    handleSeekBarState(it.isPlaying(), it.playbackSpeed)
                }

        musicServiceBinder.animatePlayPauseLiveData
                .subscribe(this) {
                    if (it == PlaybackStateCompat.STATE_PLAYING) {
                        playPauseBehavior.animationPlay(true)
                    } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                        playPauseBehavior.animationPause(true)
                    }
                }

        musicServiceBinder.observeMetadata()
                .subscribe(this) {
                    title.text = it.getTitle()
                    artist.text = it.getArtist()
                }

        musicServiceBinder.onBookmarkChangedLiveData
                .subscribe(this, this::updateProgressBarProgress)

        musicServiceBinder.onMaxChangedLiveData
                .subscribe(this, this::updateProgressBarMax)

        setupSeekBar()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        seekBar.setOnSeekBarChangeListener(null)
        playPause.setOnClickListener(null)
        subscriptions.clear()
        updateDisposable.unsubscribe()
    }

    private fun updateProgressBarProgress(progress: Long) {
        seekBar.progress = progress.toInt()
    }

    private fun updateProgressBarMax(max: Long) {
        seekBar.max = max.toInt()
    }

    private fun handleSeekBarState(isPlaying: Boolean, speed: Float){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar(speed)
        }
    }

    private fun resumeSeekBar(speed: Float){
        updateDisposable = Observable.interval(PROGRESS_BAR_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
                .subscribe({ seekBar.incrementProgressBy((PROGRESS_BAR_INTERVAL * speed).toInt()) }, Throwable::printStackTrace)
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

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}