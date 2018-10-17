package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import dev.olog.msc.presentation.widget.playpause.IPlayPauseBehavior
import dev.olog.msc.utils.k.extension.isPlaying
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

class LyricsContent (
        lifecycle: Lifecycle,
        context: Context,
        private val musicServiceBinder: MusicServiceBinder

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

        musicServiceBinder.onStateChanged()
                .subscribe({
                    handleSeekBarState(it.isPlaying(), it.playbackSpeed)
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.animatePlayPauseLiveData
                .subscribe({
                    if (it == PlaybackStateCompat.STATE_PLAYING) {
                        playPauseBehavior.animationPlay(true)
                    } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                        playPauseBehavior.animationPause(true)
                    }
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onMetadataChanged
                .subscribe({
                    title.text = it.title
                    artist.text = it.artist
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onBookmarkChangedLiveData
                .subscribe(this::updateProgressBarProgress, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onMaxChangedLiveData
                .subscribe(this::updateProgressBarMax, Throwable::printStackTrace)
                .addTo(subscriptions)

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