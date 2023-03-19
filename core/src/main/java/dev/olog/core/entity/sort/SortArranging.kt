package dev.olog.core.entity.sort

enum class SortArranging {
    ASCENDING, DESCENDING;

    override fun toString(): String {
        if (this == ASCENDING){
            return "ASC"
        }
        return "DESC"
    }

    operator fun not(): SortArranging = when (this) {
        ASCENDING -> DESCENDING
        DESCENDING -> ASCENDING
    }

}