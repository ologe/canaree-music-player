package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class PodcastAlbumSortTest {

    @Test
    fun testTypes() {
        // all
        Assert.assertEquals(PodcastAlbumSortType.Title.type, SortTypeV2.Album)
        Assert.assertEquals(PodcastAlbumSortType.Artist.type, SortTypeV2.Artist)
        Assert.assertEquals(PodcastAlbumSortType.Date.type, SortTypeV2.Date)

        // episodes
        Assert.assertEquals(PodcastAlbumEpisodesSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(PodcastAlbumEpisodesSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(PodcastAlbumEpisodesSortType.Date.type, SortTypeV2.Date)
        Assert.assertEquals(PodcastAlbumEpisodesSortType.TrackNumber.type, SortTypeV2.TrackNumber)
    }

}