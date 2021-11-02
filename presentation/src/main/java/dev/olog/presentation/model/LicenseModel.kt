package dev.olog.presentation.model

import dev.olog.core.MediaId
import dev.olog.feature.base.model.BaseModel

data class LicenseModel(
    override val type: Int,
    override val mediaId: MediaId,
    val name: String,
    val url: String,
    val license: String
) : BaseModel