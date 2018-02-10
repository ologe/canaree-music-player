package dev.olog.msc.presentation.licenses

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.MediaId

data class LicenseModel(
        override val type: Int,
        override val mediaId: MediaId,
        val name: String,
        val url: String,
        val license: String

) : BaseModel