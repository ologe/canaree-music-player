package dev.olog.domain.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import org.junit.Test

class GetPlaylistsUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = GetPlaylistsUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokeTrack() {
        // when
        sut(PlaylistType.TRACK)

        verify(playlistGateway).getAll()
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokePodcast() {
        // when
        sut(PlaylistType.PODCAST)

        verify(podcastGateway).getAll()
        verifyZeroInteractions(playlistGateway)
    }

}