package dev.olog.service.floating

import android.content.Context
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.extensions.filter
import dev.olog.shared.extensions.subscribe
import dev.olog.media.model.PlayerState
import dev.olog.shared.widgets.playpause.IPlayPauseBehavior
import dev.olog.media.widget.CustomSeekBar
import io.reactivex.disposables.CompositeDisposable
import java.lang.IllegalArgumentException

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

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .subscribe(this) {
                when (it.state){
                    PlayerState.PLAYING -> playPauseBehavior.animationPlay(true)
                    PlayerState.PAUSED -> playPauseBehavior.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid ${it.state}")
                }
            }

        glueService.observeMetadata()
            .subscribe(this) {
                title.text = it.title
                artist.text = it.artist
            }

        glueService.observeMetadata()
            .subscribe(this) {
                seekBar.max = it.duration.toInt()
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