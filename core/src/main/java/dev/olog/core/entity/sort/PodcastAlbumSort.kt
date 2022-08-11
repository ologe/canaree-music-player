package dev.olog.core.entity.sort

data class AllPodcastAlbumsSort(
    val type: PodcastAlbumSortType,
    val direction: SortDirection,
)

enum class PodcastAlbumSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Album),
    Artist(SortTypeV2.Artist),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): PodcastAlbumSortType {
            return values().first { it.type == type }
        }
    }

}

data class PodcastAlbumEpisodesSort(
    val type: PodcastAlbumEpisodesSortType,
    val direction: SortDirection,
)

enum class PodcastAlbumEpisodesSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date),
    TrackNumber(SortTypeV2.TrackNumber);

    companion object {
        operator fun invoke(type: SortTypeV2): PodcastAlbumEpisodesSortType {
            return values().first { it.type == type }
        }
    }

}