package dev.olog.presentation.base.adapter

import dev.olog.presentation.PresentationId

interface CanShowIsPlaying {

    val playingMediaId: PresentationId.Track?

    fun onCurrentPlayingChanged(adapter: ObservableAdapter<*>, item: PresentationId.Track)

}

class CanShowIsPlayingImpl : CanShowIsPlaying {

    override var playingMediaId: PresentationId.Track? = null
        private set

    override fun onCurrentPlayingChanged(adapter: ObservableAdapter<*>, item: PresentationId.Track) {
        val before = adapter.indexOf { it.mediaId == playingMediaId }
        val after = adapter.indexOf { it.mediaId == item }
        this.playingMediaId = item
        adapter.notifyItemChanged(before, false)
        adapter.notifyItemChanged(after, true)
    }

}