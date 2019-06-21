package dev.olog.msc.presentation.playing.queue.model

import dev.olog.core.MediaId
import dev.olog.presentation.model.BaseModel

data class DisplayableQueueSong (
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String? = null,
    val positionInList: String,
    val isCurrentSong: Boolean

) : BaseModel