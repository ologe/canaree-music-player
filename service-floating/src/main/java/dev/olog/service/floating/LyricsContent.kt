package dev.olog.service.floating

import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dev.olog.lib.media.model.PlayerState
import kotlinx.android.synthetic.main.content_offline_lyrics.view.*
import kotlinx.android.synthetic.main.content_web_view_with_player.view.*
import kotlinx.coroutines.flow.*

class LyricsContent(
    service: LifecycleService,
    private val glueService: MusicGlueService
) : WebViewContent(service, R.layout.content_web_view_with_player) {

    init {
        glueService.playbackState
            .onEach { content.seekBar.onStateChanged(it) }
            .launchIn(service.lifecycleScope)

        glueService.playbackState
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach {
                when (it){
                    PlayerState.PLAYING -> content.playPause.animationPlay(true)
                    PlayerState.PAUSED -> content.playPause.animationPause(true)
                    else -> error("state not valid $it")
                }
            }.launchIn(service.lifecycleScope)

        glueService.metadata
            .onEach {
                content.header.text = it.title
                content.subHeader.text = it.artist
                content.seekBar.max = it.duration.toLongMilliseconds().toInt()
            }.launchIn(service.lifecycleScope)
    }

    override fun onShown() {
        super.onShown()
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