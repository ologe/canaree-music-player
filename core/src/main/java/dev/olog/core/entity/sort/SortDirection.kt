package dev.olog.core.entity.sort

enum class SortDirection {
    ASCENDING, DESCENDING;

    override fun toString(): String {
        if (this == ASCENDING){
            return "ASC"
        }
        return "DESC"
    }

}