package dev.olog.domain.interactor.mostplayed

import com.nhaarman.mockitokotlin2.*
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.Mocks
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveMostPlayedSongsUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val genreGateway = mock<GenreGateway>()
    private val sut = ObserveMostPlayedSongsUseCase(folderGateway, playlistGateway, genreGateway)

    private val result = listOf(Mocks.song.copy(id = 10))

    @Test
    fun `test folders`() = runBlockingTest {
        val mediaId = Category(FOLDERS, 1)
        whenever(folderGateway.observeMostPlayed(mediaId))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(folderGateway).observeMostPlayed(mediaId)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test playlists`() = runBlockingTest {
        val mediaId = Category(PLAYLISTS, 1)
        whenever(playlistGateway.observeMostPlayed(mediaId))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(playlistGateway).observeMostPlayed(mediaId)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test genres`() = runBlockingTest {
        val mediaId = Category(GENRES, 1)
        whenever(genreGateway.observeMostPlayed(mediaId))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(genreGateway).observeMostPlayed(mediaId)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(genreGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test others`() = runBlockingTest {
        val allowed = listOf(
            FOLDERS, PLAYLISTS, GENRES
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }

            val mediaId = Category(value, 1)
            assertEquals(emptyList<Song>(), sut(mediaId).first())
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

}