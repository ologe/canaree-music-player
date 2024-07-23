package dev.olog.service.floating

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import dev.olog.media.model.PlayerState
import dev.olog.service.floating.databinding.ContentWebViewWithPlayerBinding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class LyricsContent(
    lifecycle: Lifecycle,
    context: Context,
    private val glueService: MusicGlueService

) : WebViewContent(lifecycle, context, R.layout.content_web_view_with_player) {

    override fun onShown() {
        super.onShown()
        val binding = ContentWebViewWithPlayerBinding.bind(content)

        glueService.observePlaybackState()
            .filterNotNull()
            .onEach {
                binding.layoutMiniPlayer.seekBar.onStateChanged(it)
            }.launchIn(lifecycleScope)

        glueService.observePlaybackState()
            .filterNotNull()
            .filter { it.isPlayOrPause }
            .map { it.state }
            .distinctUntilChanged()
            .onEach {
                when (it){
                    PlayerState.PLAYING -> binding.playPause.animationPlay(true)
                    PlayerState.PAUSED -> binding.playPause.animationPause(true)
                    else -> throw IllegalArgumentException("state not valid $it")
                }
            }
            .launchIn(lifecycleScope)

        glueService.observeMetadata()
            .filterNotNull()
            .onEach {
                binding.layoutMiniPlayer.header.text = it.title
                binding.layoutMiniPlayer.subHeader.text = it.artist
            }.launchIn(lifecycleScope)

        glueService.observeMetadata()
            .filterNotNull()
            .onEach {
                binding.layoutMiniPlayer.seekBar.max = it.duration.toInt()
            }.launchIn(lifecycleScope)

        binding.playPause.setOnClickListener { glueService.playPause() }

        binding.layoutMiniPlayer.seekBar.setListener(onProgressChanged = {}, onStartTouch = {}, onStopTouch = {
            glueService.seekTo(binding.layoutMiniPlayer.seekBar.progress.toLong())
        })
    }

    override fun onHidden() {
        super.onHidden()
        val binding = ContentWebViewWithPlayerBinding.bind(content)
        binding.playPause.setOnClickListener(null)
        binding.layoutMiniPlayer.seekBar.setOnSeekBarChangeListener(null)
    }

    override fun getUrl(item: String): String {
        return "http://www.google.it/search?q=$item+lyrics"
    }
}