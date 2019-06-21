package dev.olog.msc.presentation.special.thanks

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.core.MediaId

data class SpecialThanksModel(
    override val type: Int,
    override val mediaId: MediaId,
    val title: String,
    val image: Int
) : BaseModel