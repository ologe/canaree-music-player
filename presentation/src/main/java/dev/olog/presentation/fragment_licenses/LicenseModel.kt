package dev.olog.presentation.fragment_licenses

import dev.olog.presentation._base.BaseModel
import dev.olog.shared.MediaId

data class LicenseModel(
        override val type: Int,
        override val mediaId: MediaId,
        val name: String,
        val url: String,
        val license: String

) : BaseModel