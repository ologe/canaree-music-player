package dev.olog.presentation.model

data class DisplayableItem (
        val type: Int,
        val mediaId: String,
        val title: String,
        val subtitle: String? = null,
        val image: String = "",
        val isPlayable: Boolean = false,
        val isRemix: Boolean = false,
        val isExplicit: Boolean = false,
        val trackNumber: String = ""
)