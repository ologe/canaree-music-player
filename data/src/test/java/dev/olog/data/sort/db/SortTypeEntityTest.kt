package dev.olog.data.sort.db

import org.junit.Assert
import org.junit.Test

class SortTypeEntityTest {

    @Test
    fun test() {
        Assert.assertEquals("title", SortTypeEntity.Title.columnName)
        Assert.assertEquals("title", SortTypeEntity.Title.toString())
        Assert.assertEquals("title", SORT_TYPE_TITLE)

        Assert.assertEquals("artist", SortTypeEntity.Artist.columnName)
        Assert.assertEquals("artist", SortTypeEntity.Artist.toString())
        Assert.assertEquals("artist", SORT_TYPE_ARTIST)

        Assert.assertEquals("album", SortTypeEntity.Album.columnName)
        Assert.assertEquals("album", SortTypeEntity.Album.toString())
        Assert.assertEquals("album", SORT_TYPE_ALBUM)

        Assert.assertEquals("album_artist", SortTypeEntity.AlbumArtist.columnName)
        Assert.assertEquals("album_artist", SortTypeEntity.AlbumArtist.toString())
        Assert.assertEquals("album_artist", SORT_TYPE_ALBUM_ARTIST)

        Assert.assertEquals("duration", SortTypeEntity.Duration.columnName)
        Assert.assertEquals("duration", SortTypeEntity.Duration.toString())
        Assert.assertEquals("duration", SORT_TYPE_DURATION)

        Assert.assertEquals("date_added", SortTypeEntity.Date.columnName)
        Assert.assertEquals("date_added", SortTypeEntity.Date.toString())
        Assert.assertEquals("date_added", SORT_TYPE_DATE)

        Assert.assertEquals("track_number", SortTypeEntity.TrackNumber.columnName)
        Assert.assertEquals("track_number", SortTypeEntity.TrackNumber.toString())
        Assert.assertEquals("track_number", SORT_TYPE_TRACK_NUMBER)

        Assert.assertEquals("custom", SortTypeEntity.Custom.columnName)
        Assert.assertEquals("custom", SortTypeEntity.Custom.toString())
        Assert.assertEquals("custom", SORT_TYPE_CUSTOM)
    }

}