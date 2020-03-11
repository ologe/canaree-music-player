package dev.olog.presentation.model

import dev.olog.presentation.PresentationId

data class SpecialThanksModel(
    override val type: Int,
    override val mediaId: PresentationId.Category,
    val title: String,
    val image: Int
) : BaseModel