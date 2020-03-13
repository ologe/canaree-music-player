package dev.olog.core.interactor

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
    private val sut = InsertMostPlayedUseCase(
        folderGateway, playlistGateway, genreGateway
    )

    @Test
    fun testFolderInvoke() = runBlockingTest {
        // given
        val mediaId = Track(FOLDERS, 1, 2)

        // when
        sut(mediaId)

        verify(folderGateway).insertMostPlayed(mediaId)

        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun testPlaylistInvoke() = runBlockingTest {
        // given
        val mediaId = Track(PLAYLISTS, 1, 2)

        // when
        sut(mediaId)

        verify(playlistGateway).insertMostPlayed(mediaId)

        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun testGenreInvoke() = runBlockingTest {
        // given
        val mediaId = Track(GENRES, 1, 2)

        // when
        sut(mediaId)

        verify(genreGateway).insertMostPlayed(mediaId)

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(genreGateway)
    }

    @Test
    fun testNotAllowed() = runBlockingTest {
        val allowed = listOf(
            FOLDERS,
            PLAYLISTS,
            GENRES
        )

        for (value in values()) {
            sut(Track(value, 1, 2))
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

}