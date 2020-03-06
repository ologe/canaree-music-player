package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.entity.PlaylistType.*
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InsertCustomTrackListToPlaylistTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = InsertCustomTrackListToPlaylist(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlocking {
        // given
        val id = 1L
        val trackId = 100L
        val name = "playlist"
        val tracks = listOf(trackId)
        val request = InsertCustomTrackListToPlaylist.Input(name, tracks, PODCAST)
        whenever(podcastGateway.createPlaylist(name)).thenReturn(id)

        // when
        sut(request)

        // then
        verify(podcastGateway).createPlaylist(name)
        verify(podcastGateway).addSongsToPlaylist(id, tracks)
        verifyNoMoreInteractions(podcastGateway)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlocking {
        // given
        val id = 1L
        val trackId = 100L
        val name = "playlist"
        val tracks = listOf(trackId)
        val request = InsertCustomTrackListToPlaylist.Input(name, tracks, TRACK)
        whenever(playlistGateway.createPlaylist(name)).thenReturn(id)

        // when
        sut(request)

        // then
        verify(playlistGateway).createPlaylist(name)
        verify(playlistGateway).addSongsToPlaylist(id, tracks)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvokeAuto() = runBlocking {
        val request = InsertCustomTrackListToPlaylist.Input("any", emptyList(), AUTO)
        sut(request)
    }

}