package dev.olog.msc.presentation.playing.queue.model

import dev.olog.presentation.model.BaseModel
import dev.olog.core.MediaId

data class DisplayableQueueSong (
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val subtitle: String? = null,
    val image: String = "",
    val positionInList: String,
    val isCurrentSong: Boolean

) : BaseModel