package dev.olog.data.sort.mapper

import dev.olog.core.entity.sort.AuthorDetailSort
import dev.olog.core.entity.sort.AuthorSort
import dev.olog.data.sort.SortTypeEntity

internal fun AuthorSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toAuthorSort(): AuthorSort {
    return AuthorSort.values().first { it.serialized == this.serialized }
}

internal fun AuthorDetailSort.toEntity(): SortTypeEntity {
    return SortTypeEntity.values().first { it.serialized == this.serialized }
}

internal fun SortTypeEntity.toDetailAuthorSort(): AuthorDetailSort {
    return AuthorDetailSort.values().first { it.serialized == this.serialized }
}