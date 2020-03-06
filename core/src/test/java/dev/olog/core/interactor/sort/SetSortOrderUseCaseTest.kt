package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.core.prefs.SortPreferences
import org.junit.Assert
import org.junit.Test

class SetSortOrderUseCaseTest {

    private val gateway = mock<SortPreferences>()

    private val sut = SetSortOrderUseCase(gateway)

    @Test
    fun testFolders() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "")
        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailFolderSort(request.sortType)
    }

    @Test
    fun testPlaylists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailPlaylistSort(request.sortType)
    }

    @Test
    fun testPodcastPlaylists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_PLAYLIST, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailPlaylistSort(request.sortType)
    }

    @Test
    fun testAlbums() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailAlbumSort(request.sortType)
    }

    @Test
    fun testArtists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailArtistSort(request.sortType)
    }

    @Test
    fun testPodcastArtists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_ARTISTS, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailArtistSort(request.sortType)
    }

    @Test
    fun testGenre() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "")

        val request = SetSortOrderUseCase.Request(mediaId, SortType.TITLE)

        sut(request)
        verify(gateway).setDetailGenreSort(request.sortType)
    }

    @Test
    fun testNotAllowed() {
        val allowed = listOf(
            MediaIdCategory.FOLDERS,
            MediaIdCategory.PODCASTS_PLAYLIST,
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ARTISTS,
            MediaIdCategory.ARTISTS,
            MediaIdCategory.GENRES
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            val mediaId = MediaId.createCategoryValue(value, "")
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