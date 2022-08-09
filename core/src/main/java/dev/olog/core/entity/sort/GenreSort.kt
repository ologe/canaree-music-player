package dev.olog.core.entity.sort

data class AllGenresSort(
    val type: GenreSortType,
    val direction: SortDirection,
)

enum class GenreSortType(val type: SortTypeV2) {
    Name(SortTypeV2.Title);

    companion object {
        operator fun invoke(type: SortTypeV2): GenreSortType {
            return values().first { it.type == type }
        }
    }

}

data class GenreSongsSort(
    val type: GenreSongsSortType,
    val direction: SortDirection,
)

enum class GenreSongsSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Artist(SortTypeV2.Artist),
    Album(SortTypeV2.Album),
    AlbumArtist(SortTypeV2.AlbumArtist),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): GenreSongsSortType {
            return GenreSongsSortType.values().first { it.type == type }
        }
    }

}