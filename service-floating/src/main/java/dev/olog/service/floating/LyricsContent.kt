package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dev.olog.media.model.PlayerState
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.android.synthetic.main.content_web_view_with_player.view.*
import kotlinx.coroutines.flow.*

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player) {

    override fun onShown() {
        super.onShown()

        glueService.observePlaybackState()
            .onEach { content.seekBar.onStateChanged(it) }
            .launchIn(lifecycleScope)

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach {
                when (it){
                    PlayerState.PLAYING -> content.playPause.animationPlay(true)
                    PlayerState.PAUSED -> content.playPause.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid $it")
                }
            }.launchIn(lifecycleScope)

        glueService.observeMetadata()
            .onEach {
                content.header.text = it.title
                content.subHeader.text = it.artist
            }.launchIn(lifecycleScope)

        glueService.observeMetadata()
            .onEach {
                content.seekBar.max = it.duration.toInt()
            }.launchIn(lifecycleScope)

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