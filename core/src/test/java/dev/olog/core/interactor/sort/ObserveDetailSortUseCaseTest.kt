package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.prefs.SortPreferences
import org.junit.Assert
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
                sut(mediaId)
                Assert.fail("not allowed $mediaId")
            } catch (ex: IllegalArgumentException) {
            }
        }
        verifyZeroInteractions(gateway)
    }

}