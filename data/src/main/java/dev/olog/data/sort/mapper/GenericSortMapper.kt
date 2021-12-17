package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.GenericSort
import dev.olog.data.sort.SortTypeEntity

internal fun GenericSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toGenericSort(): GenericSort {
    return GenericSort.values().first { it.serialized == this.serialized }
}