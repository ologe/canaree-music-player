package dev.olog.presentation.model

import dev.olog.domain.entity.SmallPlayType
import dev.olog.presentation._base.BaseModel
import dev.olog.shared.MediaId

data class DisplayableItem (
        override val type: Int,
        override val mediaId: MediaId,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val isPlayable: Boolean = false,
        val isRemix: Boolean = false,
        val isExplicit: Boolean = false,
        val trackNumber: String = "",
        val smallPlayType: SmallPlayType? = null

) : BaseModel