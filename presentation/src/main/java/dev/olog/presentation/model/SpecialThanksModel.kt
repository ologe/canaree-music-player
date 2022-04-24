package dev.olog.presentation.model

import dev.olog.core.MediaId
import dev.olog.platform.adapter.BaseModel

data class SpecialThanksModel(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val image: Int
) : BaseModel