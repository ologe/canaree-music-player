package dev.olog.presentation.model

import dev.olog.feature.presentation.base.model.BaseModel
import dev.olog.feature.presentation.base.model.PresentationId

data class SpecialThanksModel(
    override val type: Int,
    override val mediaId: PresentationId.Category,
    val title: String,
    val image: Int
) : BaseModel