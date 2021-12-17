package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.FolderDetailSort
import dev.olog.data.sort.SortTypeEntity

internal fun FolderDetailSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toDetailFolderSort(): FolderDetailSort {
    return FolderDetailSort.values().first { it.serialized == this.serialized }
}