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

class DeleteRecentSearchUseCaseTest {

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = DeleteRecentSearchUseCase(gateway)

    @Test
    fun testDeleteTrack() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = SONGS_CATEGORY.playableItem(id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteTrack(mediaId)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePodcast() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = PODCAST_CATEGORY.playableItem(id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteTrack(mediaId)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteArtist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ARTISTS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteArtist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteAlbum() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(ALBUMS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteAlbum(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePlaylist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteFolder() = runBlockingTest {
        // given
        val id = 123L
        val mediaId = Category(FOLDERS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteFolder(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteGenre() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(GENRES, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteGenre(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePodcastPlaylist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePodcastPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }


    @Test
    fun testDeletePodcastArtist() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_AUTHORS, id)

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePodcastArtist(id)
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