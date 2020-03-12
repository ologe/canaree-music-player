package dev.olog.image.provider.fetcher

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GlideAlbumFetcherTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val albumId = 10L
    private val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, albumId)
    private val gateway = mock<ImageRetrieverGateway>()
    private val sut = GlideAlbumFetcher(context, mediaId, gateway, mock())

    @Test
    fun testExecute() = coroutineRule.runBlocking {
        // given
        val expectedImage = "image"
        val lastFmAlbum = LastFmAlbum(
            id = albumId,
            title = "",
            artist = "",
            image = expectedImage,
            mbid = "",
            wiki = ""
        )
        whenever(gateway.getAlbum(albumId)).thenReturn(lastFmAlbum)

        // when
        val image = sut.execute()

        // then
        verify(gateway).getAlbum(albumId)
        assertEquals(
            expectedImage,
            image
        )
    }

    @Test
    fun testMustFetchTrue() = coroutineRule.runBlocking {
        // given
        whenever(gateway.mustFetchAlbum(albumId)).thenReturn(true)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchAlbum(albumId)
        assertEquals(
            true,
            actual
        )
    }

    @Test
    fun testMustFetchFalse() = coroutineRule.runBlocking {
        // given
        whenever(gateway.mustFetchAlbum(albumId)).thenReturn(false)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchAlbum(albumId)
        assertEquals(
            false,
            actual
        )
    }

}