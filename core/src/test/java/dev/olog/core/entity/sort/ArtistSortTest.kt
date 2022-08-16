package dev.olog.core.entity.sort

import org.junit.Assert
import org.junit.Test

class ArtistSortTest {

    @Test
    fun testTypes() {
        // all
        Assert.assertEquals(ArtistSortType.Name.type, SortTypeV2.Artist)
        Assert.assertEquals(ArtistSortType.Date.type, SortTypeV2.Date)

        // songs
        Assert.assertEquals(ArtistSongsSortType.Title.type, SortTypeV2.Title)
        Assert.assertEquals(ArtistSongsSortType.Album.type, SortTypeV2.Album)
        Assert.assertEquals(ArtistSongsSortType.AlbumArtist.type, SortTypeV2.AlbumArtist)
        Assert.assertEquals(ArtistSongsSortType.Duration.type, SortTypeV2.Duration)
        Assert.assertEquals(ArtistSongsSortType.Date.type, SortTypeV2.Date)
        Assert.assertEquals(ArtistSongsSortType.TrackNumber.type, SortTypeV2.TrackNumber)
    }

}