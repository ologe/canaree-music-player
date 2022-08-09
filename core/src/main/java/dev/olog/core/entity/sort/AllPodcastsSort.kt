package dev.olog.core.entity.sort

data class AllPodcastsSort(
    val type: PodcastSortType,
    val direction: SortDirection,
)

enum class PodcastSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Artist(SortTypeV2.Artist),
    Album(SortTypeV2.Album),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): PodcastSortType {
            return values().first { it.type == type }
        }
    }

}