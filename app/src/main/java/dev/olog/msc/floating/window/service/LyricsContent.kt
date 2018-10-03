package dev.olog.msc.floating.window.service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import dev.olog.msc.R
import dev.olog.msc.constants.AppConstants.PROGRESS_BAR_INTERVAL
import dev.olog.msc.domain.interactor.IsRepositoryEmptyUseCase
import dev.olog.msc.floating.window.service.music.service.MusicServiceBinder
import dev.olog.msc.presentation.widget.AnimatedImageView
import dev.olog.msc.presentation.widget.playpause.IPlayPauseBehavior
import dev.olog.msc.utils.k.extension.isPlaying
import dev.olog.msc.utils.k.extension.toggleVisibility
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

class LyricsContent (
        lifecycle: Lifecycle,
        context: Context,
        private val musicServiceBinder: MusicServiceBinder,
        isRepositoryEmptyUseCase: IsRepositoryEmptyUseCase

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player), DefaultLifecycleObserver {

    private val next = content.findViewById<AnimatedImageView>(R.id.next)
    private val playPauseBehavior = content.findViewById<ImageButton>(R.id.playPause) as IPlayPauseBehavior
    private val playPause = content.findViewById<ImageButton>(R.id.playPause)
    private val previous = content.findViewById<AnimatedImageView>(R.id.previous)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val title = content.findViewById<TextView>(R.id.title)
    private val artist = content.findViewById<TextView>(R.id.artist)
    private val noTracks = content.findViewById<View>(R.id.noTracks)

    private val subscriptions = CompositeDisposable()
    private var updateDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        next.setOnClickListener { musicServiceBinder.next() }
        playPause.setOnClickListener { musicServiceBinder.playPause() }
        previous.setOnClickListener { musicServiceBinder.previous() }

        isRepositoryEmptyUseCase.execute()
                .subscribe({ noTracks.toggleVisibility(it, true) }, Throwable::printStackTrace)
                .addTo(subscriptions)

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

        musicServiceBinder.animateSkipToLiveData
                .subscribe(this::animateSkipTo, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.skipToNextVisibility
                .subscribe(next::updateVisibility, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.skipToPreviousVisibility
                .subscribe(previous::updateVisibility, Throwable::printStackTrace)
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
        next.setOnClickListener(null)
        playPause.setOnClickListener(null)
        previous.setOnClickListener(null)
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

    private fun animateSkipTo(toNext: Boolean) {
        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
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