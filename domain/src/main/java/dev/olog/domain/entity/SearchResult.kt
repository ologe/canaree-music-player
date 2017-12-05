package dev.olog.domain.entity

data class SearchResult(
        val mediaId: String,
        val itemType: Int,
        val title: String,
        val image: String
)