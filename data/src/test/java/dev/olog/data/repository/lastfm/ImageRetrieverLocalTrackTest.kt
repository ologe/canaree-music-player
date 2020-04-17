package dev.olog.data.repository.lastfm

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.data.db.LastFmDao
import dev.olog.data.mapper.millisToFormattedDate
import dev.olog.data.model.db.LastFmTrackEntity
import dev.olog.domain.entity.LastFmTrack
import org.junit.Assert.*
import org.junit.Test

internal class ImageRetrieverLocalTrackTest {

    private val mockEntity = LastFmTrackEntity(
        1, "title", "artist", "album", "image",
        millisToFormattedDate(System.currentTimeMillis()),
        "mbid", "artistMbid", "albumMbid"
    )

    private val mockDomain = LastFmTrack(
        1, "title", "artist", "album", "image",
        "mbid", "artistMbid", "albumMbid"
    )

    private val dao = mock<LastFmDao>()
    val sut = ImageRetrieverLocalTrack(dao)

    @Test
    fun testMustFetchShouldReturnTrue() {
        // given
        val id = 1L
        whenever(dao.getTrack(id)).thenReturn(mockEntity)

        val actual = sut.mustFetch(1)
        assertFalse(actual)
    }

    @Test
    fun testMustFetchShoulReturnFalse() {
        // given
        val id = 1L
        whenever(dao.getTrack(id)).thenReturn(null)

        // when
        val actual = sut.mustFetch(1)

        // then
        assertTrue(actual)
    }

    @Test
    fun testGetCachedShouldReturnValue() {
        // given
        val id = 1L
        whenever(dao.getTrack(id)).thenReturn(mockEntity)

        // when
        val actual = sut.getCached(id)

        // then
        assertEquals(mockDomain, actual)
    }

    @Test
    fun testGetCachedShouldReturnNull() {
        // given
        val id = 1L
        whenever(dao.getTrack(id)).thenReturn(null)

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
        verify(dao).insertTrack(mockEntity)
    }

    @Test
    fun deleteItem() {
        // given
        val id = 1L

        // when
        sut.delete(id)

        verify(dao).deleteTrack(id)
    }

}