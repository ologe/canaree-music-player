package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InsertMostPlayedUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val genreGateway = mock<GenreGateway>()
    private val sut = InsertMostPlayedUseCase(
        folderGateway, playlistGateway, genreGateway
    )

    @Test
    fun testFolderInvoke() = runBlocking {
        // given
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "")

        // when
        sut(mediaId)

        verify(folderGateway).insertMostPlayed(mediaId)

        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun testPlaylistInvoke() = runBlocking {
        // given
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, "")

        // when
        sut(mediaId)

        verify(playlistGateway).insertMostPlayed(mediaId)

        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

    @Test
    fun testGenreInvoke() = runBlocking {
        // given
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "")

        // when
        sut(mediaId)

        verify(genreGateway).insertMostPlayed(mediaId)

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(genreGateway)
    }

    @Test
    fun testNotAllowed() = runBlocking {
        val allowed = listOf(
            MediaIdCategory.FOLDERS,
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.GENRES
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            sut(MediaId.createCategoryValue(value, ""))
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(genreGateway)
    }

}