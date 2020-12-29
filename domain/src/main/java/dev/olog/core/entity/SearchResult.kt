package dev.olog.core.entity

import dev.olog.core.MediaId
import dev.olog.core.RecentSearchesType

data class SearchResult(
    val mediaId: MediaId,
    val itemType: RecentSearchesType,
    val title: String
)