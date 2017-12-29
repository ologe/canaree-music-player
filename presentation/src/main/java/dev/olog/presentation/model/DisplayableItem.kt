package dev.olog.presentation.model

import dev.olog.shared.MediaId

data class DisplayableItem (
        val type: Int,
        val mediaId: MediaId,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val isPlayable: Boolean = false,
        val isRemix: Boolean = false,
        val isExplicit: Boolean = false,
        val trackNumber: String = ""
)