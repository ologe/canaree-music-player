package dev.olog.presentation.offlinelyrics2

import dev.olog.core.MediaId
import dev.olog.presentation.model.BaseModel

data class LyricsModel(
    override val type: Int,
    override val mediaId: MediaId,
    val content: String,
    val isCurrent: Boolean
) : BaseModel