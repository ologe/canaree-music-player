package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import org.junit.Assert
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
    fun testPodcastPlaylists() {
        val mediaId = Category(PODCASTS_PLAYLIST, 1)

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
    fun testPodcastArtists() {
        val mediaId = Category(PODCASTS_AUTHORS, 1)

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

    @Test
    fun testNotAllowed() {
        val allowed = listOf(
            FOLDERS,
            PODCASTS_PLAYLIST,
            PLAYLISTS,
            ALBUMS,
            PODCASTS_AUTHORS,
            ARTISTS,
            GENRES
        )

        for (value in values()) {
            if (value in allowed) {
                continue
            }
            val mediaId = Category(value, 1)
            try {
                val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
                Assert.fail("not allowed $mediaId")
            } catch (ex: IllegalArgumentException) {
            }
        }
        verifyZeroInteractions(gateway)
    }

}