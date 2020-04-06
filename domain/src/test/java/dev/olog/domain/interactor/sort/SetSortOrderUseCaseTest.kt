package dev.olog.domain.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.catchIaeOnly
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.prefs.SortPreferences
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class SetSortOrderUseCaseTest {

    private val gateway = mock<SortPreferences>()

    private val sut = SetSortOrderUseCase(gateway)

    @Test
    fun testFolders() {
        val mediaId = Category(FOLDERS, 1)
        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailFolderSort(request.sortType)
    }

    @Test
    fun testPlaylists() {
        val mediaId = Category(PLAYLISTS, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailPlaylistSort(request.sortType)
    }

    @Test
    fun testAlbums() {
        val mediaId = Category(ALBUMS, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailAlbumSort(request.sortType)
    }

    @Test
    fun testArtists() {
        val mediaId = Category(ARTISTS, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailArtistSort(request.sortType)
    }


    @Test
    fun testGenre() {
        val mediaId = Category(GENRES, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailGenreSort(request.sortType)
    }

    // region special cases

    @Test
    fun testPodcastPlaylists() {
        val mediaId = Category(PODCASTS_PLAYLIST, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verifyZeroInteractions(gateway)
    }

    @Test
    fun testPodcastArtists() {
        val mediaId = Category(PODCASTS_AUTHORS, 1)

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verifyZeroInteractions(gateway)
    }

    // endregion

    @Test
    fun testNotAllowed() = runBlockingTest {
        val allowed = listOf(
            FOLDERS,
            PODCASTS_PLAYLIST,
            PLAYLISTS,
            ALBUMS,
            PODCASTS_AUTHORS,
            ARTISTS,
            GENRES
        )

        values().catchIaeOnly(allowed) { value ->
            val mediaId = Category(value, 1)
            val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

            sut(request)
        }
        verifyZeroInteractions(gateway)
    }

}