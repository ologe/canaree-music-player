package dev.olog.domain.entity

import dev.olog.shared.MediaId

data class SearchResult(
        val mediaId: MediaId,
        val itemType: Int,
        val title: String,
        val image: String
)