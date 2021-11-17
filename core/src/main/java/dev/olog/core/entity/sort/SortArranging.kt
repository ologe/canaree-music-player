package dev.olog.core.entity.sort

enum class SortArranging {
    ASCENDING, DESCENDING;

    override fun toString(): String = when (this) {
        ASCENDING -> "ASC"
        DESCENDING -> "DESC"
    }

}