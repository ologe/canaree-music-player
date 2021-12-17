package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.PlaylistDetailSort
import dev.olog.data.sort.SortTypeEntity

internal fun PlaylistDetailSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toDetailPlaylistSort(): PlaylistDetailSort {
    return PlaylistDetailSort.values().first { it.serialized == this.serialized }
}