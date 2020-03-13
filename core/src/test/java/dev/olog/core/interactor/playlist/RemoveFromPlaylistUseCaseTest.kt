package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.entity.PlaylistType.PODCAST
import dev.olog.core.entity.PlaylistType.TRACK
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class RemoveFromPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RemoveFromPlaylistUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlockingTest {
        // given
        val id = 1L
        val trackId = 10L
        val input = RemoveFromPlaylistUseCase.Input(id, trackId, PODCAST)

        // when
        sut(input)

        verify(podcastGateway).removeFromPlaylist(id, trackId)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlockingTest {
        // given
        val id = 1L
        val trackId = 10L
        val input = RemoveFromPlaylistUseCase.Input(id, trackId, TRACK)

        // when
        sut(input)

        verify(playlistGateway).removeFromPlaylist(id, trackId)
        verifyZeroInteractions(podcastGateway)
    }

}