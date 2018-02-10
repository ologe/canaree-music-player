package dev.olog.msc.presentation.thanks

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.MediaId

data class SpecialThanksModel(
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val image: Int
) : BaseModel