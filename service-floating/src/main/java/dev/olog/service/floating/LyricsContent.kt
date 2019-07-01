package dev.olog.service.floating

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.media.getArtist
import dev.olog.media.getDuration
import dev.olog.media.getTitle
import dev.olog.shared.extensions.subscribe
import dev.olog.shared.widgets.playpause.IPlayPauseBehavior
import dev.olog.shared.widgets.progressbar.CustomSeekBar
import io.reactivex.disposables.CompositeDisposable

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player),
    DefaultLifecycleObserver {

    private val playPauseBehavior =
        content.findViewById<ImageButton>(R.id.playPause) as IPlayPauseBehavior
    private val playPause = content.findViewById<ImageButton>(R.id.playPause)
    private val seekBar = content.findViewById<CustomSeekBar>(R.id.seekBar)
    private val title = content.findViewById<TextView>(R.id.header)
    private val artist = content.findViewById<TextView>(R.id.subHeader)

    private val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        playPause.setOnClickListener { glueService.playPause() }

        glueService.observePlaybackState()
            .subscribe(this) {
                seekBar.onStateChanged(it)
            }

        glueService.animatePlayPauseLiveData
            .subscribe(this) {
                if (it == PlaybackStateCompat.STATE_PLAYING) {
                    playPauseBehavior.animationPlay(true)
                } else if (it == PlaybackStateCompat.STATE_PAUSED) {
                    playPauseBehavior.animationPause(true)
                }
            }

        glueService.observeMetadata()
            .subscribe(this) {
                title.text = it.getTitle()
                artist.text = it.getArtist()
            }

        glueService.observeMetadata()
            .subscribe(this) {
                seekBar.max = it.getDuration().toInt()
            }

        seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(seekBar.progress.toLong())
        })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        seekBar.setOnSeekBarChangeListener(null)
        playPause.setOnClickListener(null)
        subscriptions.clear()
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}