package dev.olog.core.entity.sort

data class AllFoldersSort(
    val type: FolderSortType,
    val direction: SortDirection,
)

enum class FolderSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title);

    companion object {
        operator fun invoke(type: SortTypeV2): FolderSortType {
            return values().first { it.type == type }
        }
    }

}

data class FolderSongsSort(
    val type: FolderSongsSortType,
    val direction: SortDirection,
)

enum class FolderSongsSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Artist(SortTypeV2.Artist),
    Album(SortTypeV2.Album),
    AlbumArtist(SortTypeV2.AlbumArtist),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date),
    TrackNumber(SortTypeV2.TrackNumber);

    companion object {
        operator fun invoke(type: SortTypeV2): FolderSongsSortType {
            return values().first { it.type == type }
        }
    }

}