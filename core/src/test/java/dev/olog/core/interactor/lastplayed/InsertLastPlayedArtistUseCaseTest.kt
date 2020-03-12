package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.ARTISTS
import dev.olog.core.MediaIdCategory.PODCASTS_AUTHORS
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
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
        sut(Category(ARTISTS, id))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithPodcast() = runBlocking {
        // given
        val id = 1L

        val gateway = mock<PodcastAuthorGateway>()

        val sut = InsertLastPlayedArtistUseCase(mock(), gateway)

        // when
        sut(Category(PODCASTS_AUTHORS, id))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithOtherCategories() = runBlocking {
        // given
        val id = 1L
        val allowed = listOf(ARTISTS, PODCASTS_AUTHORS)

        val sut = InsertLastPlayedArtistUseCase(mock(), mock())

        // when
        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                sut(Category(value, id))
                fail("can handle only $allowed, instead was $value")
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }

}