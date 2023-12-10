package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.Lifecycle
import dev.olog.media.model.PlayerState
import dev.olog.service.floating.databinding.ContentWebViewWithPlayerBinding
import dev.olog.shared.android.extensions.distinctUntilChanged
import dev.olog.shared.android.extensions.filter
import dev.olog.shared.android.extensions.map
import dev.olog.shared.android.extensions.subscribe

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player) {

    private val binding = ContentWebViewWithPlayerBinding.bind(content)

    override fun onShown() {
        super.onShown()

        glueService.observePlaybackState()
            .subscribe(this) {
                binding.layoutMiniPlayer.seekBar.onStateChanged(it)
            }

        glueService.observePlaybackState()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .subscribe(this) {
                when (it){
                    PlayerState.PLAYING -> binding.playPause.animationPlay(true)
                    PlayerState.PAUSED -> binding.playPause.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid $it")
                }
            }

        glueService.observeMetadata()
            .subscribe(this) {
                binding.layoutMiniPlayer.header.text = it.title
                binding.layoutMiniPlayer.subHeader.text = it.artist
            }

        glueService.observeMetadata()
            .subscribe(this) {
                binding.layoutMiniPlayer.seekBar.max = it.duration.toInt()
            }

        binding.playPause.setOnClickListener { glueService.playPause() }

        binding.layoutMiniPlayer.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(binding.layoutMiniPlayer.seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()
        binding.playPause.setOnClickListener(null)
        binding.layoutMiniPlayer.seekBar.setOnSeekBarChangeListener(null)
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}