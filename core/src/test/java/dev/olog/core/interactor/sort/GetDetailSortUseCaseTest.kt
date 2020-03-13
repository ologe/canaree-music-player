package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.catchIaeOnly
import dev.olog.core.prefs.SortPreferences
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class GetDetailSortUseCaseTest {

    private val gateway = mock<SortPreferences>()

    private val sut = GetDetailSortUseCase(gateway)

    @Test
    fun testFolders() {
        val mediaId = Category(FOLDERS, 1)

        sut(mediaId)

        verify(gateway).getDetailFolderSort()
    }

    @Test
    fun testPlaylists() {
        val mediaId = Category(PLAYLISTS, 1)

        sut(mediaId)
        verify(gateway).getDetailPlaylistSort()
    }

    @Test
    fun testAlbums() {
        val mediaId = Category(ALBUMS, 1)

        sut(mediaId)
        verify(gateway).getDetailAlbumSort()
    }

    @Test
    fun testArtists() {
        val mediaId = Category(ARTISTS, 1)

        sut(mediaId)
        verify(gateway).getDetailArtistSort()
    }

    @Test
    fun testGenre() {
        val mediaId = Category(GENRES, 1)

        sut(mediaId)
        verify(gateway).getDetailGenreSort()
    }

    // region special cases

    @Test
    fun testPodcastPlaylists() {
        val mediaId = Category(PODCASTS_PLAYLIST, 1)

        sut(mediaId)
        verifyZeroInteractions(gateway)
    }

    @Test
    fun testPodcastArtists() {
        val mediaId = Category(PODCASTS_AUTHORS, 1)

        sut(mediaId)
        verifyZeroInteractions(gateway)
    }

    // end region

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
            sut(mediaId)
        }

        verifyZeroInteractions(gateway)
    }

}