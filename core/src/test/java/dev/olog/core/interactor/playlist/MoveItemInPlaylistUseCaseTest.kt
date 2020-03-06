package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.entity.PlaylistType.*
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MoveItemInPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = MoveItemInPlaylistUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlocking {
        // given
        val id = 1L
        val from = 10
        val to = 11
        val moves = listOf(from to to)
        val input = MoveItemInPlaylistUseCase.Input(id, moves, PODCAST)

        // when
        sut(input)

        verify(podcastGateway).moveItem(id, moves)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlocking {
        // given
        val id = 1L
        val from = 10
        val to = 11
        val moves = listOf(from to to)
        val input = MoveItemInPlaylistUseCase.Input(id, moves, TRACK)

        // when
        sut(input)

        verify(playlistGateway).moveItem(id, moves)
        verifyZeroInteractions(podcastGateway)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvokeAuto() = runBlocking {
        // given
        val input = MoveItemInPlaylistUseCase.Input(1, emptyList(), AUTO)

        // when
        sut(input)
    }

}