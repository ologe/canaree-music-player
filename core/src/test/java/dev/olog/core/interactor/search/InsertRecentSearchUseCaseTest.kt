package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.core.MediaId.Companion.SONGS_CATEGORY
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.RecentSearchesGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InsertRecentSearchUseCaseTest {

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = InsertRecentSearchUseCase(gateway)

    @Test
    fun testInsertTrack() = runBlocking {
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
    fun testInsertArtist() = runBlocking {
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
    fun testInsertAlbum() = runBlocking {
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
    fun testInsertPlaylist() = runBlocking {
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
    fun testInsertFolder() = runBlocking {
        // given
        val id = "path".hashCode().toLong()
        val mediaId = Category(FOLDERS, 321)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertFolder(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertGenre() = runBlocking {
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
    fun testInsertPodcast() = runBlocking {
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
    fun testInsertPodcastPlaylist() = runBlocking {
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
    fun testInsertPodcastArtist() = runBlocking {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_AUTHORS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastArtist(id)
        verifyNoMoreInteractions(gateway)
    }

}