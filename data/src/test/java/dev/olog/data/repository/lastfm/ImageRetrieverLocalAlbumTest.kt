package dev.olog.lib.repository.lastfm

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.domain.entity.LastFmAlbum
import dev.olog.lib.db.LastFmDao
import dev.olog.lib.model.db.LastFmAlbumEntity
import dev.olog.lib.mapper.millisToFormattedDate
import org.junit.Assert.*
import org.junit.Test

internal class ImageRetrieverLocalAlbumTest {

    private val mockEntity = LastFmAlbumEntity(
        1, "title", "artist", "image",
        millisToFormattedDate(System.currentTimeMillis()),
        "mbid", "wiki"
    )

    private val mockDomain = LastFmAlbum(
        1, "title", "artist",
        "image", "mbid", "wiki"
    )

    private val dao = mock<LastFmDao>()
    val sut = ImageRetrieverLocalAlbum(dao)

    @Test
    fun testMustFetchShouldReturnTrue() {
        // given
        val id = 1L
        whenever(dao.getAlbum(id)).thenReturn(mockEntity)

        val actual = sut.mustFetch(1)
        assertFalse(actual)
    }

    @Test
    fun testMustFetchShoulReturnFalse() {
        // given
        val id = 1L
        whenever(dao.getAlbum(id)).thenReturn(null)

        // when
        val actual = sut.mustFetch(1)

        // then
        assertTrue(actual)
    }

    @Test
    fun testGetCachedShouldReturnValue() {
        // given
        val id = 1L
        whenever(dao.getAlbum(id)).thenReturn(mockEntity)

        // when
        val actual = sut.getCached(id)

        // then
        assertEquals(mockDomain, actual)
    }

    @Test
    fun testGetCachedShouldReturnNull() {
        // given
        val id = 1L
        whenever(dao.getAlbum(id)).thenReturn(null)

        // when
        val actual = sut.getCached(id)

        // then
        assertNull(actual)
    }

    @Test
    fun testCacheItem() {
        // when
        sut.cache(mockDomain)

        // then
        verify(dao).insertAlbum(mockEntity)
    }

    @Test
    fun deleteItem() {
        // given
        val id = 1L

        // when
        sut.delete(id)

        verify(dao).deleteAlbum(id)
    }

}