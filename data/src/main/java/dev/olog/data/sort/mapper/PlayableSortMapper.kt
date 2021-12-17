package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.PlayableSort
import dev.olog.data.sort.SortTypeEntity

internal fun PlayableSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toPlayableSortSort(): PlayableSort {
    return PlayableSort.values().first { it.serialized == this.serialized }
}