package dev.olog.image.provider.fetcher

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GlideArtistFetcherTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val artistId = 10L
    private val mediaId = MediaId.Category(MediaIdCategory.ARTISTS, artistId)
    private val gateway = mock<ImageRetrieverGateway>()
    private val sut = GlideArtistFetcher(context, mediaId, gateway, mock())

    @Test
    fun testExecute() = coroutineRule.runBlocking {
        // given
        val expectedImage = "image"
        val lastFmArtist = LastFmArtist(
            id = artistId,
            image = expectedImage,
            mbid = "",
            wiki = ""
        )
        whenever(gateway.getArtist(artistId)).thenReturn(lastFmArtist)

        // when
        val image = sut.execute()

        // then
        verify(gateway).getArtist(artistId)
        assertEquals(
            expectedImage,
            image
        )
    }

    @Test
    fun testExecuteLastFmPlaceholder() = coroutineRule.runBlocking {
        // given
        val artistImage = GlideArtistFetcher.LAST_FM_PLACEHOLDER
        val lastFmArtist = LastFmArtist(
            id = artistId,
            image = artistImage,
            mbid = "",
            wiki = ""
        )
        whenever(gateway.getArtist(artistId)).thenReturn(lastFmArtist)

        // when
        val image = sut.execute()

        // then
        verify(gateway).getArtist(artistId)
        assertEquals(
            "",
            image
        )
    }

    @Test
    fun testExecuteDeezerPlaceholder() = coroutineRule.runBlocking {
        // given
        val artistImage = GlideArtistFetcher.DEEZER_PLACEHOLDER
        val lastFmArtist = LastFmArtist(
            id = artistId,
            image = artistImage,
            mbid = "",
            wiki = ""
        )
        whenever(gateway.getArtist(artistId)).thenReturn(lastFmArtist)

        // when
        val image = sut.execute()

        // then
        verify(gateway).getArtist(artistId)
        assertEquals(
            "",
            image
        )
    }

    @Test
    fun testMustFetchTrue() = coroutineRule.runBlocking {
        // given
        whenever(gateway.mustFetchArtist(artistId)).thenReturn(true)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchArtist(artistId)
        assertEquals(
            true,
            actual
        )
    }

    @Test
    fun testMustFetchFalse() = coroutineRule.runBlocking {
        // given
        whenever(gateway.mustFetchArtist(artistId)).thenReturn(false)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchArtist(artistId)
        assertEquals(
            false,
            actual
        )
    }

}