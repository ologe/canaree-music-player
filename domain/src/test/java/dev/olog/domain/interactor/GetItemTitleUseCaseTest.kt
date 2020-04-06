package dev.olog.domain.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.Mocks
import dev.olog.domain.catchIaeOnly
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetItemTitleUseCaseTest {

    private val expected = "title"

    private val folderGateway = mock<FolderGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.folder.copy(title = expected))
    }
    private val playlistGateway = mock<PlaylistGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.playlist.copy(title = expected))
    }
    private val albumGateway = mock<AlbumGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.album.copy(title = expected))
    }
    private val artistGateway = mock<ArtistGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.artist.copy(name = expected))
    }
    private val genreGateway = mock<GenreGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.genre.copy(name = expected))
    }

    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.podcastPlaylist.copy(title = expected))
    }
    private val podcastAuthorGateway = mock<PodcastAuthorGateway> {
        on { observeByParam(any()) } doReturn flowOf(Mocks.podcastAuthor.copy(name = expected))
    }

    private val sut = GetItemTitleUseCase(
        folderGateway, playlistGateway, albumGateway, artistGateway, genreGateway,
        podcastPlaylistGateway, podcastAuthorGateway
    )

    @Test
    fun testFolders() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(FOLDERS, id)

        // when
        val title = sut(mediaId).first()

        // then
        verify(folderGateway).observeByParam(mediaId.categoryId)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }

    @Test
    fun testPlaylists() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        // when
        val title = sut(mediaId).first()

        // then
        verify(playlistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)

    }

    @Test
    fun testAlbums() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ALBUMS, id)

        // when
        val title = sut(mediaId).first()

        // then
        verify(albumGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }

    @Test
    fun testArtists() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ARTISTS, id)

        // when
        val title = sut(mediaId).first()


        // then
        verify(artistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyNoMoreInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }

    @Test
    fun testGenres() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(GENRES, id)

        // when
        val title = sut(mediaId).first()

        // then
        verify(genreGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyNoMoreInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }


    @Test
    fun testPodcastPlaylists() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        val title = sut(mediaId).first()

        // then
        verify(podcastPlaylistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }

    @Test
    fun testPodcastArtists() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_AUTHORS, id)

        // when
        val title = sut(mediaId).first()


        // then
        verify(podcastAuthorGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyNoMoreInteractions(podcastAuthorGateway)

        assertEquals(expected, title)
    }

    @Test
    fun testNotAllowed() = runBlockingTest {
        // given
        val allowed = listOf(
            FOLDERS,
            PLAYLISTS,
            ALBUMS,
            ARTISTS,
            GENRES,
            PODCASTS_PLAYLIST,
            PODCASTS_AUTHORS
        )

        values().catchIaeOnly(allowed) { value ->
            val mediaId = Category(value, 1)

            // when
            sut(mediaId).first()
        }


        // then
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastAuthorGateway)
    }

}