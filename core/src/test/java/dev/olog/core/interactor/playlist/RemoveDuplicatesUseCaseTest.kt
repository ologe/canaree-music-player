package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RemoveDuplicatesUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RemoveDuplicatesUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlocking {
        // given
        val id = 1L
        val mediaId = Category(
            PODCASTS_PLAYLIST, id
        )

        // when
        sut(mediaId)

        verify(podcastGateway).removeDuplicated(id)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlocking {
        // given
        val id = 1L
        val mediaId = Category(
            PLAYLISTS, id
        )

        // when
        sut(mediaId)

        verify(playlistGateway).removeDuplicated(id)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokeAuto() = runBlocking {
        // given
        val allowed = listOf(
            PODCASTS_PLAYLIST, PLAYLISTS
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                sut(Category(value, 1))
                Assert.fail("invalid $value")
            } catch (ex: IllegalArgumentException) {
            }
        }
        verifyZeroInteractions(podcastGateway)
        verifyZeroInteractions(playlistGateway)
    }

}