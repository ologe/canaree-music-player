package dev.olog.msc.presentation.playing.queue.model

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.MediaId

data class DisplayableQueueSong (
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val positionInList: String,
        val isCurrentSong: Boolean

) : BaseModel