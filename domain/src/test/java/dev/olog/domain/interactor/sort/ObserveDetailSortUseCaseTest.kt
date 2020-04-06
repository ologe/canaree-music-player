package dev.olog.domain.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.catchIaeOnly
import dev.olog.domain.prefs.SortPreferences
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ObserveDetailSortUseCaseTest {

    private val gateway = mock<SortPreferences>()

    private val sut = ObserveDetailSortUseCase(gateway)

    @Test
    fun testFolders() {
        val mediaId = Category(FOLDERS, 1)

        sut(mediaId)

        verify(gateway).observeDetailFolderSort()
    }

    @Test
    fun testPlaylists() {
        val mediaId = Category(PLAYLISTS, 1)

        sut(mediaId)
        verify(gateway).observeDetailPlaylistSort()
    }

    @Test
    fun testAlbums() {
        val mediaId = Category(ALBUMS, 1)

        sut(mediaId)
        verify(gateway).observeDetailAlbumSort()
    }

    @Test
    fun testArtists() {
        val mediaId = Category(ARTISTS, 1)

        sut(mediaId)
        verify(gateway).observeDetailArtistSort()
    }

    @Test
    fun testGenre() {
        val mediaId = Category(GENRES, 1)

        sut(mediaId)
        verify(gateway).observeDetailGenreSort()
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
            sut(mediaId)

        }
        verifyZeroInteractions(gateway)
    }

}