package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.RecentSearchesGateway
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DeleteRecentSearchUseCaseTest {

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = DeleteRecentSearchUseCase(gateway)

    @Test
    fun testDeleteTrack() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.SONGS, -1), id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteSong(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteArtist() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.ARTISTS, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteArtist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteAlbum() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.ALBUMS, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteAlbum(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePlaylist() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PLAYLISTS, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteFolder() = runBlocking {
        // given
        val id = "path".hashCode().toLong()
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.FOLDERS, 3213
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteFolder(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeleteGenre() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.GENRES, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deleteGenre(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePodcast() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS, -1), id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePodcast(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testDeletePodcastPlaylist() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_PLAYLIST, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePodcastPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }


    @Test
    fun testDeletePodcastArtist() = runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_AUTHORS, id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).deletePodcastArtist(id)
        verifyNoMoreInteractions(gateway)
    }

}