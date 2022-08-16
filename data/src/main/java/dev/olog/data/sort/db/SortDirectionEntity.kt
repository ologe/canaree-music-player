package dev.olog.data.sort.db

import dev.olog.core.entity.sort.SortDirection

const val SORT_DIRECTION_ASC = "asc"
const val SORT_DIRECTION_DESC = "desc"

enum class SortDirectionEntity(val serializedValue: String) {
    Ascending(SORT_DIRECTION_ASC),
    Descending(SORT_DIRECTION_DESC);

    companion object {
        operator fun invoke(direction: SortDirection): SortDirectionEntity {
            return when (direction) {
                SortDirection.ASCENDING -> Ascending
                SortDirection.DESCENDING -> Descending
            }
        }
    }

    fun toDomain(): SortDirection {
        return when (this) {
             Ascending -> SortDirection.ASCENDING
             Descending -> SortDirection.DESCENDING
        }
    }

    override fun toString(): String = serializedValue

}