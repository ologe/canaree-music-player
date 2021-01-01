package dev.olog.domain.entity

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.RecentSearchesType

data class SearchResult(
    val mediaId: MediaId,
    val itemType: RecentSearchesType,
    val title: String
)