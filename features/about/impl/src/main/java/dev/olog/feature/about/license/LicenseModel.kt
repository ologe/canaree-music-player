package dev.olog.feature.about.license

import dev.olog.core.MediaId
import dev.olog.platform.adapter.BaseModel

data class LicenseModel(
    override val type: Int,
    override val mediaId: MediaId,
    val name: String,
    val url: String,
    val license: String
) : BaseModel