package dev.olog.feature.about.model

import dev.olog.feature.presentation.base.model.BaseModel
import dev.olog.feature.presentation.base.model.PresentationId

internal data class LicenseModel(
    override val type: Int,
    override val mediaId: PresentationId.Category,
    val name: String,
    val url: String,
    val license: String
) : BaseModel