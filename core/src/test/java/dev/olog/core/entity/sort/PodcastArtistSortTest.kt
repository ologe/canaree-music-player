package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class PodcastArtistSortTest {

    @Test
    fun testTypes() {
        // all
        Assert.assertEquals(PodcastArtistSortType.Name.type, SortTypeV2.Artist)
        Assert.assertEquals(PodcastArtistSortType.Date.type, SortTypeV2.Date)

        // episodes
        Assert.assertEquals(PodcastArtistEpisodesSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(PodcastArtistEpisodesSortType.Album.type, SortTypeV2.Album)
        Assert.assertEquals(PodcastArtistEpisodesSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(PodcastArtistEpisodesSortType.Date.type, SortTypeV2.Date)
        Assert.assertEquals(PodcastArtistEpisodesSortType.TrackNumber.type, SortTypeV2.TrackNumber)
    }

}