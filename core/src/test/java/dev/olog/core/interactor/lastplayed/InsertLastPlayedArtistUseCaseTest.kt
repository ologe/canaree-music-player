package dev.olog.core.interactor.lastplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class InsertLastPlayedArtistUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvokeWithTrack() = coroutineRule.runBlocking {
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
    fun testInvokeWithPodcast() = coroutineRule.runBlocking {
        // given
        val id = 1L

        val gateway = mock<PodcastArtistGateway>()

        val sut = InsertLastPlayedArtistUseCase(mock(), gateway)

        // when
        sut(MediaId.createCategoryValue(MediaIdCategory.PODCASTS_ARTISTS, id.toString()))

        // then
        verify(gateway).addLastPlayed(id)
    }

    @Test
    fun testInvokeWithOtherCategories() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val allowed = listOf(MediaIdCategory.ARTISTS, MediaIdCategory.PODCASTS_ARTISTS)

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