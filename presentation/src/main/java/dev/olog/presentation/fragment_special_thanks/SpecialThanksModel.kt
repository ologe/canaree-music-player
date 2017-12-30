package dev.olog.presentation.fragment_special_thanks

import dev.olog.presentation._base.BaseModel
import dev.olog.shared.MediaId

data class SpecialThanksModel(
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val image: Int
) : BaseModel