package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.entity.PlaylistType.*
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RemoveFromPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RemoveFromPlaylistUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlocking {
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
    fun testInvokeTrack() = runBlocking {
        // given
        val id = 1L
        val trackId = 10L
        val input = RemoveFromPlaylistUseCase.Input(id, trackId, TRACK)

        // when
        sut(input)

        verify(playlistGateway).removeFromPlaylist(id, trackId)
        verifyZeroInteractions(podcastGateway)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvokeAuto() = runBlocking {
        // given
        val input = RemoveFromPlaylistUseCase.Input(1, 1, AUTO)

        // when
        sut(input)
    }

}