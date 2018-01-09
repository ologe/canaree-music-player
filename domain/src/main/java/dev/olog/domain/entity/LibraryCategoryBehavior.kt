package dev.olog.domain.entity

data class LibraryCategoryBehavior(
        val category: String,
        var enabled: Boolean,
        var order: Int
)