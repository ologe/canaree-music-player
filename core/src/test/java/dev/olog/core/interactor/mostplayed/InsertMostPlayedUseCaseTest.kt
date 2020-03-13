package dev.olog.core.interactor.mostplayed

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Track
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class InsertMostPlayedUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val genreGateway = mock<GenreGateway>()
    private val sut = InsertMostPlayedUseCase(folderGateway, playlistGateway, genreGateway)

    @Test
    fun `test folders`() = runBlockingTest {
        val mediaId = Track(FOLDERS, 1, 2)
        sut(mediaId)

        verify(folderGateway).insertMostPlayed(mediaId)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun `test playlists`() = runBlockingTest {
        val mediaId = Track(PLAYLISTS, 1, 2)
        sut(mediaId)

        verify(playlistGateway).insertMostPlayed(mediaId)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun `test genres`() = runBlockingTest {
        val mediaId = Track(GENRES, 1, 2)
        sut(mediaId)

        verify(genreGateway).insertMostPlayed(mediaId)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(genreGateway)
    }

    @Test
    fun `test others`() = runBlockingTest {
        val allowed = listOf(
            FOLDERS, PLAYLISTS, GENRES
        )

        for (value in values()) {
            if (value in allowed) {
                continue
            }

            val mediaId = Track(value, 1, 2)
            sut(mediaId)
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

}