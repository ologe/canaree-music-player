package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.CollectionDetailSort
import dev.olog.core.entity.sort.CollectionSort
import dev.olog.data.sort.SortTypeEntity

internal fun CollectionSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toCollectionSort(): CollectionSort {
    return CollectionSort.values().first { it.serialized == this.serialized }
}

internal fun CollectionDetailSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toDetailCollectionSort(): CollectionDetailSort {
    return CollectionDetailSort.values().first { it.serialized == this.serialized }
}