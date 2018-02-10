package dev.olog.msc.domain.entity

data class LibraryCategoryBehavior(
        val category: String,
        var enabled: Boolean,
        var order: Int
)