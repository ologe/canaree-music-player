package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class GenreSortTest {

    @Test
    fun testTypes() {
        // all
        Assert.assertEquals(GenreSortType.Name.type, SortTypeV2.Title)

        // songs
        Assert.assertEquals(GenreSongsSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(GenreSongsSortType.Artist.type, SortTypeV2.Artist)
        Assert.assertEquals(GenreSongsSortType.Album.type, SortTypeV2.Album)
        Assert.assertEquals(GenreSongsSortType.AlbumArtist.type, SortTypeV2.AlbumArtist)
        Assert.assertEquals(GenreSongsSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(GenreSongsSortType.Date.type, SortTypeV2.Date)
    }

}