package dev.olog.floating_info

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import android.widget.TextView
import dev.olog.floating_info.di.ServiceLifecycle
import dev.olog.floating_info.music_service.MusicServiceBinder
import dev.olog.shared.unsubscribe
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.rx.SeekBarObservable
import dev.olog.shared_android.widget.AnimatedImageView
import dev.olog.shared_android.widget.AnimatedPlayPauseImageView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.ofType
import java.util.concurrent.TimeUnit

class LyricsContent (
        context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val musicServiceBinder: MusicServiceBinder

) : WebViewContent(context, R.layout.content_web_view_with_player), DefaultLifecycleObserver {

    private val next = content.findViewById<AnimatedImageView>(R.id.next)
    private val playPause = content.findViewById<AnimatedPlayPauseImageView>(R.id.playPause)
    private val previous = content.findViewById<AnimatedImageView>(R.id.previous)
    private val header = content.findViewById<TextView>(R.id.header)
    private val seekBar = content.findViewById<SeekBar>(R.id.seekBar)
    private val bookmark = content.findViewById<TextView>(R.id.bookmark)
    private val duration = content.findViewById<TextView>(R.id.duration)

    private val subscriptions = CompositeDisposable()
    private var updateDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
        next.setOnClickListener { musicServiceBinder.next() }
        playPause.setOnClickListener { musicServiceBinder.playPause() }
        previous.setOnClickListener { musicServiceBinder.previous() }

        musicServiceBinder.onMetadataChanged
                .subscribe({ header.text = it.get() }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.onMaxChangedObservable
                .subscribe({
                    duration.text = it.asString
                    seekBar.max = it.asInt
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.animatePlayPauseLiveData
                .subscribe({
                    handleSeekbarState(it == PlaybackStateCompat.STATE_PLAYING)
                    if (it == PlaybackStateCompat.STATE_PLAYING) {
                        playPause.animationPlay(true)
                    } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                        playPause.animationPause(true)
                    }
                }, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.animateSkipToLiveData
                .subscribe(this::animateSkipTo, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.skipToNextVisibility
                .subscribe(next::toggleVisibility, Throwable::printStackTrace)
                .addTo(subscriptions)

        musicServiceBinder.skipToPreviousVisibility
                .subscribe(previous::toggleVisibility, Throwable::printStackTrace)
                .addTo(subscriptions)

        setupSeekBar()
    }

    private fun handleSeekbarState(isPlaying: Boolean){
        updateDisposable.unsubscribe()
        if (isPlaying) {
            resumeSeekBar()
        }
    }

    private fun resumeSeekBar(){
        updateDisposable = Observable.interval(250, TimeUnit.MILLISECONDS)
                .subscribe({ seekBar.incrementProgressBy(250) }, Throwable::printStackTrace)
    }

    private fun animateSkipTo(toNext: Boolean) {
        if (toNext) {
            next.playAnimation()
        } else {
            previous.playAnimation()
        }
    }

    private fun setupSeekBar(){
        val seekBarObservable = SeekBarObservable(seekBar).share()

        seekBarObservable
                .ofType<Int>()
                .map { it.toLong() }
                .map { TextUtils.getReadableSongLength(it) }
                .subscribe({ bookmark.text = it }, Throwable::printStackTrace)
                .addTo(subscriptions)

        seekBarObservable.ofType<Pair<SeekBarObservable.Notification, Int>>()
                .filter { (notification, _) -> notification == SeekBarObservable.Notification.STOP }
                .map { (_, progress) -> progress.toLong() }
                .subscribe(musicServiceBinder::seekTo, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        next.setOnClickListener(null)
        playPause.setOnClickListener(null)
        previous.setOnClickListener(null)
        subscriptions.clear()
        updateDisposable.unsubscribe()
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}