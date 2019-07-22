package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.media.model.PlayerState
import dev.olog.shared.extensions.distinctUntilChanged
import dev.olog.shared.extensions.filter
import dev.olog.shared.extensions.map
import dev.olog.shared.extensions.subscribe
import kotlinx.android.synthetic.main.content_web_view_with_player.view.*
import kotlinx.android.synthetic.main.layout_mini_player.view.*

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player),
    DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
        content.playPause.setOnClickListener { glueService.playPause() }

        glueService.observePlaybackState()
            .subscribe(this) {
                content.seekBar.onStateChanged(it)
            }

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .subscribe(this) {
                when (it){
                    PlayerState.PLAYING -> content.playPause.animationPlay(true)
                    PlayerState.PAUSED -> content.playPause.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid $it")
                }
            }

        glueService.observeMetadata()
            .subscribe(this) {
                content.header.text = it.title
                content.subHeader.text = it.artist
            }

        glueService.observeMetadata()
            .subscribe(this) {
                content.seekBar.max = it.duration.toInt()
            }

        content.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
        })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        content.seekBar.setOnSeekBarChangeListener(null)
        content.playPause.setOnClickListener(null)
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}