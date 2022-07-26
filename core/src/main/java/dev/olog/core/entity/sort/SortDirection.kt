package dev.olog.core.entity.sort

import dev.olog.core.Migration

@Migration
enum class SortDirection {
    ASCENDING, DESCENDING;

    override fun toString(): String {
        if (this == ASCENDING){
            return "ASC"
        }
        return "DESC"
    }

}