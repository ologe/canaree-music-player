package dev.olog.core.entity.sort

data class AllPodcastArtistsSort(
    val type: PodcastArtistSortType,
    val direction: SortDirection,
)

enum class PodcastArtistSortType(val type: SortTypeV2) {
    Name(SortTypeV2.Artist),
    Date(SortTypeV2.Date);

    companion object {
        operator fun invoke(type: SortTypeV2): PodcastArtistSortType {
            return values().first { it.type == type }
        }
    }

}

data class PodcastArtistEpisodesSort(
    val type: PodcastArtistEpisodesSortType,
    val direction: SortDirection,
)

enum class PodcastArtistEpisodesSortType(val type: SortTypeV2) {
    Title(SortTypeV2.Title),
    Album(SortTypeV2.Album),
    Duration(SortTypeV2.Duration),
    Date(SortTypeV2.Date),
    TrackNumber(SortTypeV2.TrackNumber);

    companion object {
        operator fun invoke(type: SortTypeV2): PodcastArtistEpisodesSortType {
            return values().first { it.type == type }
        }
    }

}