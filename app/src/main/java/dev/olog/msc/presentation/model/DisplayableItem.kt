package dev.olog.msc.presentation.model

import dev.olog.msc.presentation.base.BaseModel
import dev.olog.msc.utils.MediaId

data class DisplayableItem (
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val isPlayable: Boolean = false,
        val isRemix: Boolean = false,
        val isExplicit: Boolean = false,
        val trackNumber: String = ""

) : BaseModel