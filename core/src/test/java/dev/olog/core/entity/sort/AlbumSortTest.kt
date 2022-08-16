package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class AlbumSortTest {

    @Test
    fun testTypes() {
        // all
        Assert.assertEquals(AlbumSortType.Title.type, SortTypeV2.Album)
        Assert.assertEquals(AlbumSortType.Artist.type, SortTypeV2.Artist)
        Assert.assertEquals(AlbumSortType.Date.type, SortTypeV2.Date)

        // songs
        Assert.assertEquals(AlbumSongsSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(AlbumSongsSortType.AlbumArtist.type, SortTypeV2.AlbumArtist)
        Assert.assertEquals(AlbumSongsSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(AlbumSongsSortType.Date.type, SortTypeV2.Date)
        Assert.assertEquals(AlbumSongsSortType.TrackNumber.type, SortTypeV2.TrackNumber)
    }

}