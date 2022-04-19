package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.Lifecycle
import dev.olog.media.model.PlayerState
import dev.olog.shared.android.extensions.collectOnLifecycle
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.android.synthetic.main.content_web_view_with_player.view.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player) {

    override fun onShown() {
        super.onShown()

        glueService.observePlaybackState()
            .collectOnLifecycle(this) {
                content.seekBar.onStateChanged(it)
            }

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .collectOnLifecycle(this) {
                when (it){
                    PlayerState.PLAYING -> content.playPause.animationPlay(true)
                    PlayerState.PAUSED -> content.playPause.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid $it")
                }
            }

        glueService.observeMetadata()
            .collectOnLifecycle(this) {
                content.header.text = it.title
                content.subHeader.text = it.artist
            }

        glueService.observeMetadata()
            .collectOnLifecycle(this) {
                content.seekBar.max = it.duration.toInt()
            }

        content.playPause.setOnClickListener { glueService.playPause() }

        content.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(content.seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()
        content.playPause.setOnClickListener(null)
        content.seekBar.setOnSeekBarChangeListener(null)
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}