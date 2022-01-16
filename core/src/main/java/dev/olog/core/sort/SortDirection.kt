package dev.olog.core.sort

// TODO update Sort.sq
enum class SortDirection(val serialized: String) {
    ASCENDING("asc"),
    DESCENDING("desc");

    fun invert(): SortDirection = when (this) {
        ASCENDING -> DESCENDING
        DESCENDING -> ASCENDING
    }

}