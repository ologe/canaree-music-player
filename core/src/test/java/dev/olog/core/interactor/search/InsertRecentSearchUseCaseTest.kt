package dev.olog.core.interactor.search

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Rule
import org.junit.Test

class InsertRecentSearchUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val gateway = mock<RecentSearchesGateway>()
    private val sut = InsertRecentSearchUseCase(gateway)

    @Test
    fun testInsertTrack() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.SONGS, ""), id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertSong(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertArtist() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.ARTISTS, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertArtist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertAlbum() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.ALBUMS, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertAlbum(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPlaylist() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PLAYLISTS, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertFolder() = coroutineRule.runBlocking {
        // given
        val id = "path".hashCode().toLong()
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.FOLDERS, "path"
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertFolder(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertGenre() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.GENRES, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertGenre(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcast() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS, ""), id
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcast(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcastPlaylist() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_PLAYLIST, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastPlaylist(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcastAlbum() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_ALBUMS, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastAlbum(id)
        verifyNoMoreInteractions(gateway)
    }

    @Test
    fun testInsertPodcastArtist() = coroutineRule.runBlocking {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(
            MediaIdCategory.PODCASTS_ARTISTS, id.toString()
        )

        // when
        sut(mediaId)

        // then
        verify(gateway).insertPodcastArtist(id)
        verifyNoMoreInteractions(gateway)
    }

}