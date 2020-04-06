package dev.olog.domain.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.domain.MediaId.Companion.SONGS_CATEGORY
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.catchIaeOnly
import dev.olog.domain.gateway.RecentSearchesGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class InsertRecentSearchUseCaseTest {

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = InsertRecentSearchUseCase(gateway)

    @Test
    fun testInsertTrack() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = SONGS_CATEGORY.playableItem(id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertTrack(mediaId)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcast() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = PODCAST_CATEGORY.playableItem(id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertTrack(mediaId)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertArtist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ARTISTS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertArtist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertAlbum() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ALBUMS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertAlbum(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPlaylist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertFolder() = runBlockingTest {
        // given
        val id = 123L
        val mediaId = Category(FOLDERS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertFolder(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertGenre() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(GENRES, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertGenre(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcastPlaylist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcastArtist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_AUTHORS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastArtist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun `test invalid categories`() = runBlockingTest {
        val allowed = listOf(
            FOLDERS, PLAYLISTS, ALBUMS, ARTISTS, GENRES, PODCASTS_PLAYLIST, PODCASTS_AUTHORS
        )

        values().catchIaeOnly(allowed) { value ->
            val mediaId = Category(value, 1)
            sut(mediaId)
        }
        verifyZeroInteractions(gateway)
    }

}