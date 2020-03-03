package dev.olog.presentation.base.adapter

import dev.olog.core.MediaId

interface CanShowIsPlaying {

    val playingMediaId: MediaId?

    fun onCurrentPlayingChanged(adapter: ObservableAdapter<*>, item: MediaId)

}

class CanShowIsPlayingImpl : CanShowIsPlaying {

    override var playingMediaId: MediaId? = null
        private set

    override fun onCurrentPlayingChanged(adapter: ObservableAdapter<*>, item: MediaId) {
        val before = adapter.indexOf { it.mediaId == playingMediaId }
        val after = adapter.indexOf { it.mediaId == item }
        this.playingMediaId = item
        adapter.notifyItemChanged(before, false)
        adapter.notifyItemChanged(after, true)
    }

}