package dev.olog.image.provider.fetcher

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GlideSongFetcherTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val context = mock<Context>()
    private val songId = 10L
    private val mediaId = MediaId.Category(MediaIdCategory.ALBUMS, 1)
        .playableItem(songId)
    private val gateway = mock<ImageRetrieverGateway>()
    private val sut = GlideSongFetcher(context, mediaId, gateway, mock())

    @Test
    fun testExecute() = coroutineRule.runBlockingTest {
        // given
        val expectedImage = "image"
        val lastFmTrack = LastFmTrack(
            id = songId,
            title = "",
            artist = "",
            album = "",
            image = expectedImage,
            mbid = "",
            albumMbid = "",
            artistMbid = ""
        )
        whenever(gateway.getTrack(songId)).thenReturn(lastFmTrack)

        // when
        val image = sut.execute()

        // then
        verify(gateway).getTrack(songId)
        assertEquals(
            expectedImage,
            image
        )
    }

    @Test
    fun testMustFetchTrue() = coroutineRule.runBlockingTest {
        // given
        whenever(gateway.mustFetchTrack(songId)).thenReturn(true)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchTrack(songId)
        assertEquals(
            true,
            actual
        )
    }

    @Test
    fun testMustFetchFalse() = coroutineRule.runBlockingTest {
        // given
        whenever(gateway.mustFetchTrack(songId)).thenReturn(false)

        // when
        val actual = sut.mustFetch()

        // then
        verify(gateway).mustFetchTrack(songId)
        assertEquals(
            false,
            actual
        )
    }

}