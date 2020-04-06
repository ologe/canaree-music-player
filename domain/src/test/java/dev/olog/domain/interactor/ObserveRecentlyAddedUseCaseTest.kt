package dev.olog.domain.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.MediaIdCategory.FOLDERS
import dev.olog.domain.MediaIdCategory.GENRES
import dev.olog.domain.Mocks
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveRecentlyAddedUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val genreGateway = mock<GenreGateway>()
    private val sut = ObserveRecentlyAddedUseCase(folderGateway, genreGateway)

    private val result = listOf(Mocks.song.copy(id = 10))

    @Test
    fun `test folders`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(FOLDERS, id)
        whenever(folderGateway.observeRecentlyAdded(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(folderGateway).observeRecentlyAdded(id)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(genreGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test genres`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(GENRES, id)
        whenever(genreGateway.observeRecentlyAdded(id))
            .thenReturn(flowOf(result))

        val actual = sut(mediaId).first()

        verify(genreGateway).observeRecentlyAdded(id)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(genreGateway)

        assertEquals(result, actual)
    }

    @Test
    fun `test others`() = runBlockingTest {
        val allowed = listOf(
            FOLDERS, GENRES
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }

            val mediaId = Category(value, 1)
            assertEquals(emptyList<Song>(), sut(mediaId).first())
        }

        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(genreGateway)
    }

}