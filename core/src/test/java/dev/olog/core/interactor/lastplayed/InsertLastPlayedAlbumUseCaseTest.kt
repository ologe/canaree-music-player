package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.MediaIdCategory.PODCASTS_ALBUMS
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class InsertLastPlayedAlbumUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvokeWithTrack() = coroutineRule.runBlocking {
        // given
        val id = 1L

        val gateway = mock<AlbumGateway>()

        val sut = InsertLastPlayedAlbumUseCase(gateway, mock())

        // when
        sut(MediaId.createCategoryValue(ALBUMS, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithPodcast() = coroutineRule.runBlocking {
        // given
        val id = 1L

        val gateway = mock<PodcastAlbumGateway>()

        val sut = InsertLastPlayedAlbumUseCase(mock(), gateway)

        // when
        sut(MediaId.createCategoryValue(PODCASTS_ALBUMS, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithOtherCategories() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val allowed = listOf(ALBUMS, PODCASTS_ALBUMS)

        val sut = InsertLastPlayedAlbumUseCase(mock(), mock())

        // when
        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                sut(MediaId.createCategoryValue(value, id.toString()))
                fail("can handle only $allowed, instead was $value")
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }

}