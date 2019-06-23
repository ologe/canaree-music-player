package dev.olog.core.entity

import dev.olog.core.MediaId

data class SearchResult(
    val mediaId: MediaId,
    val itemType: Int,
    val title: String
)