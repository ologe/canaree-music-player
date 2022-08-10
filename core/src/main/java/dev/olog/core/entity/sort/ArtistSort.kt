package dev.olog.core.entity.sort

data class AllArtistsSort(
    val type: ArtistSortType,
    val direction: SortDirection,
)

enum class ArtistSortType(val type: SortTypeV2) {
    Name(SortTypeV2.Artist),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): ArtistSortType {
            return values().first { it.type == type }
        }
    }

}

data class ArtistSongsSort(
    val type: ArtistSongsSortType,
    val direction: SortDirection,
)

enum class ArtistSongsSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Album(SortTypeV2.Album),
    AlbumArtist(SortTypeV2.AlbumArtist),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date),
    TrackNumber(SortTypeV2.TrackNumber);

    companion object {
        operator fun invoke(type: SortTypeV2): ArtistSongsSortType {
            return values().first { it.type == type }
        }
    }

}