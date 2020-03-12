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
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test

class ClearPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = ClearPlaylistUseCase(
        playlistGateway, podcastGateway
    )

    @Test
    fun testInvokePodcast() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        sut(mediaId)

        // then
        verify(podcastGateway).clearPlaylist(id)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        // when
        sut(mediaId)

        // then
        verify(playlistGateway).clearPlaylist(id)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokeWithWrongMediaId() = runBlockingTest {
        // given
        val allowed = listOf(
            PLAYLISTS, PODCASTS_PLAYLIST
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                val mediaId = Category(value, 1)

                // when
                sut(mediaId)
                Assert.fail("only $allowed is allow, instead was $value")
            } catch (ex: IllegalArgumentException) {
            }
        }

        // then
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
    }

}