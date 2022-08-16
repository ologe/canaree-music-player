package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class PodcastSortTest {

    @Test
    fun testTypes() {
        Assert.assertEquals(PodcastSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(PodcastSortType.Artist.type, SortTypeV2.Artist)
        Assert.assertEquals(PodcastSortType.Album.type, SortTypeV2.Album)
        Assert.assertEquals(PodcastSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(PodcastSortType.Date.type, SortTypeV2.Date)
    }

}