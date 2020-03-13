package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.Mocks
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveRelatedArtistsUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val genreGateway = mock<GenreGateway>()
    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway>()
    private val sut = ObserveRelatedArtistsUseCase(
        folderGateway, playlistGateway, genreGateway, podcastPlaylistGateway)

    private val result = listOf(Mocks.artist.copy(id = 10))

    @Test
    fun `test folders`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(FOLDERS, id)
        whenever(folderGateway.observeRelatedArtists(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(folderGateway).observeRelatedArtists(id)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
        verifyZeroInteractions(podcastPlaylistGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test playlist`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)
        whenever(playlistGateway.observeRelatedArtists(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(playlistGateway).observeRelatedArtists(id)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
        verifyZeroInteractions(podcastPlaylistGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test genres`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(GENRES, id)
        whenever(genreGateway.observeRelatedArtists(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(genreGateway).observeRelatedArtists(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(genreGateway)
        verifyZeroInteractions(podcastPlaylistGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test podcast playlist`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)
        whenever(podcastPlaylistGateway.observeRelatedArtists(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(podcastPlaylistGateway).observeRelatedArtists(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
        verifyNoMoreInteractions(podcastPlaylistGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test others`() = runBlockingTest {
        val allowed = listOf(
            FOLDERS, PLAYLISTS, GENRES, PODCASTS_PLAYLIST
        )

        for (value in values()) {
            if (value in allowed) {
                continue
            }

            val mediaId = Category(value, 1)
            assertEquals(emptyList<Song>(), sut(mediaId).first())
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
    }

}