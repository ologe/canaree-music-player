package dev.olog.core.entity.sort

data class AllAlbumsSort(
    val type: AlbumSortType,
    val direction: SortDirection,
)

enum class AlbumSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Album),
    Artist(SortTypeV2.Artist),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): AlbumSortType {
            return values().first { it.type == type }
        }
    }

}

data class AlbumSongsSort(
    val type: AlbumSongsSortType,
    val direction: SortDirection,
)

enum class AlbumSongsSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    AlbumArtist(SortTypeV2.AlbumArtist),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date),
    TrackNumber(SortTypeV2.TrackNumber);

    companion object {
        operator fun invoke(type: SortTypeV2): AlbumSongsSortType {
            return values().first { it.type == type }
        }
    }

}