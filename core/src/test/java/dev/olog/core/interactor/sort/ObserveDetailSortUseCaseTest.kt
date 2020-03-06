package dev.olog.core.interactor.sort

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.prefs.SortPreferences
import org.junit.Assert
import org.junit.Test

class ObserveDetailSortUseCaseTest {

    private val gateway = mock<SortPreferences>()

    private val sut = ObserveDetailSortUseCase(gateway)

    @Test
    fun testFolders() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "")

        sut(mediaId)

        verify(gateway).observeDetailFolderSort()
    }

    @Test
    fun testPlaylists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, "")

        sut(mediaId)
        verify(gateway).observeDetailPlaylistSort()
    }

    @Test
    fun testPodcastPlaylists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_PLAYLIST, "")

        sut(mediaId)
        verify(gateway).observeDetailPlaylistSort()
    }

    @Test
    fun testAlbums() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "")

        sut(mediaId)
        verify(gateway).observeDetailAlbumSort()
    }

    @Test
    fun testArtists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "")

        sut(mediaId)
        verify(gateway).observeDetailArtistSort()
    }

    @Test
    fun testPodcastArtists() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_ARTISTS, "")

        sut(mediaId)
        verify(gateway).observeDetailArtistSort()
    }

    @Test
    fun testGenre() {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "")

        sut(mediaId)
        verify(gateway).observeDetailGenreSort()
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
                sut(mediaId)
                Assert.fail("not allowed $mediaId")
            } catch (ex: IllegalArgumentException) {
            }
        }
        verifyZeroInteractions(gateway)
    }

}