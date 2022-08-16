package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class SongSortTest {

    @Test
    fun testTypes() {
        Assert.assertEquals(SongSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(SongSortType.Artist.type, SortTypeV2.Artist)
        Assert.assertEquals(SongSortType.Album.type, SortTypeV2.Album)
        Assert.assertEquals(SongSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(SongSortType.Date.type, SortTypeV2.Date)
    }

}