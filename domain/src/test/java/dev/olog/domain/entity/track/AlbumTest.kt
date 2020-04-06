package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.ALBUMS
import dev.olog.domain.MediaIdCategory.ARTISTS
import dev.olog.domain.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumTest {

    @Test
    fun testGetMediaId() {
        val id = 1L
        val album = Mocks.album.copy(id = id)
        assertEquals(
            Category(ALBUMS, id),
            album.mediaId
        )
    }

    @Test
    fun testGetArtistMediaId() {
        val id = 1L
        val album = Mocks.album.copy(artistId = id)
        assertEquals(
            Category(ARTISTS, id),
            album.artistMediaId
        )
    }

}