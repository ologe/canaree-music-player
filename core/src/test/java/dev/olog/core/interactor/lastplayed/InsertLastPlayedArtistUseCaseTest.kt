package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.ArtistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test

class InsertLastPlayedArtistUseCaseTest {

    @Test
    fun testInvokeWithTrack() = runBlocking {
        // given
        val id = 1L

        val gateway = mock<ArtistGateway>()

        val sut = InsertLastPlayedArtistUseCase(gateway, mock())

        // when
        sut(MediaId.createCategoryValue(MediaIdCategory.ARTISTS, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithPodcast() = runBlocking {
        // given
        val id = 1L

        val gateway = mock<PodcastArtistGateway>()

        val sut = InsertLastPlayedArtistUseCase(mock(), gateway)

        // when
        sut(MediaId.createCategoryValue(MediaIdCategory.PODCASTS_AUTHOR, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithOtherCategories() = runBlocking {
        // given
        val id = 1L
        val allowed = listOf(MediaIdCategory.ARTISTS, MediaIdCategory.PODCASTS_AUTHOR)

        val sut = InsertLastPlayedArtistUseCase(mock(), mock())

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