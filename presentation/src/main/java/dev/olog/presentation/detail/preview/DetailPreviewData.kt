package dev.olog.presentation.detail.preview

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.detail.adapter.DetailItem
import dev.olog.presentation.detail.adapter.DetailSongMode
import dev.olog.shared.TextUtils

object DetailPreviewData {

    fun detailHeader(
        mediaId: MediaId = MediaId.songId(1),
        title: String = "Angel Beach",
        subtitle: String = "Trilogy",
        biography: String? = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    ) = DetailItem.DetailHeader(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        biography = biography,
    )

    fun songHeader(
        sortType: SortType = SortType.TITLE,
        sortArranging: SortArranging = SortArranging.ASCENDING,
    ) = DetailItem.HeaderSongs(MediaId.songId(1), SortEntity(sortType, sortArranging))
    fun shuffle() = DetailItem.Shuffle(MediaId.songId(1))

    fun song(
        mediaId: MediaId = MediaId.songId(1),
        title: String = "Angel Beach",
        subtitle: String = "Trilogy",
        mode: DetailSongMode? = null,
    ) = DetailItem.Song(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        mode = mode
    )

    fun songFooter(): DetailItem.Footer {
        return DetailItem.Footer("3 Songs${TextUtils.MIDDLE_DOT_SPACED}9 Minutes")
    }

    fun relatedArtistsHeader(showSeeAll: Boolean = true): DetailItem.HeaderRelatedArtists {
        return DetailItem.HeaderRelatedArtists(
            mediaId = MediaId.songId(1),
            showSeeAll = showSeeAll
        )
    }

    fun relatedArtistsList(itemCount: Int = 20): DetailItem.RelatedArtistsList {
        val list = buildList {
            repeat(itemCount) {
                add(album(MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1")))
            }
        }
        return DetailItem.RelatedArtistsList(list)
    }

    fun siblingsHeader(): DetailItem.HeaderSiblings {
        return DetailItem.HeaderSiblings("Other albums")
    }

    fun siblingsList(itemCount: Int = 20): DetailItem.SiblingsList {
        val list = buildList {
            repeat(itemCount) {
                add(album())
            }
        }
        return DetailItem.SiblingsList(list)
    }

    fun mostPlayedHeader(): DetailItem.Header {
        return DetailItem.Header("Most Played")
    }

    fun mostPlayedList(itemCount: Int = 6): DetailItem.MostPlayedList {
        val list = buildList {
            repeat(itemCount) {
                this += DetailItem.MostPlayed(
                    mediaId = MediaId.songId(1),
                    title = "Angel Beach",
                    subtitle = "Trilogy",
                    position = "${it + 1}"
                )
            }
        }
        return DetailItem.MostPlayedList(list)
    }

    fun recentlyAddedHeader(itemCount: Int = 6, showSeeAll: Boolean = true): DetailItem.HeaderRecentlyAdded {
        return DetailItem.HeaderRecentlyAdded(
            mediaId = MediaId.songId(1),
            itemsCount = itemCount,
            showSeeAll = showSeeAll
        )
    }

    fun recentlyAddedList(itemCount: Int = 6): DetailItem.RecentlyAddedList {
        val list = buildList {
            repeat(itemCount) {
                this += DetailItem.RecentlyAdded(
                    mediaId = MediaId.songId(1),
                    title = "Angel Beach",
                    subtitle = "Trilogy",
                )
            }
        }
        return DetailItem.RecentlyAddedList(list)
    }

    fun album(
        mediaId: MediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "1"),
        title: String = "Angel Beach",
        subtitle: String = "Trilogy",
    ) = DetailItem.Album(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
    )

}