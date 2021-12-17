package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.GenreDetailSort
import dev.olog.data.sort.SortTypeEntity

internal fun GenreDetailSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toDetailGenreSort(): GenreDetailSort {
    return GenreDetailSort.values().first { it.serialized == this.serialized }
}