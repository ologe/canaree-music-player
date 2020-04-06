package dev.olog.domain.entity

import dev.olog.domain.MediaId

data class SearchResult(
    val mediaId: MediaId,
    val itemType: Int,
    val title: String
)