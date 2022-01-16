package dev.olog.core.search

import dev.olog.core.MediaUri

data class SearchResult(
    val uri: MediaUri,
    val title: String
)