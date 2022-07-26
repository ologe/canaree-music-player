package dev.olog.core.entity.sort

data class AllSongsSort(
    val type: SongSortType,
    val direction: SortDirection,
)

enum class SongSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Artist(SortTypeV2.Artist),
    Album(SortTypeV2.Album),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): SongSortType {
            return values().first { it.type == type }
        }
    }

}