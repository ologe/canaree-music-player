package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import org.junit.Assert
import org.junit.Test

class GetItemTitleUseCaseTest {

    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val albumGateway = mock<AlbumGateway>()
    private val artistGateway = mock<ArtistGateway>()
    private val genreGateway = mock<GenreGateway>()

    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway>()
    private val podcastArtistGateway = mock<PodcastAuthorGateway>()

    private val sut = GetItemTitleUseCase(
        folderGateway, playlistGateway, albumGateway, artistGateway, genreGateway,
        podcastPlaylistGateway, podcastArtistGateway
    )

    @Test
    fun testFolders() {
        // given
        val path = "path"
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, path)

        // when
        sut(mediaId)

        // then
        verify(folderGateway).observeByParam(path)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPlaylists() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, id.toString())

        // when
        sut(mediaId)

        // then
        verify(playlistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testAlbums() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, id.toString())

        // when
        sut(mediaId)

        // then
        verify(albumGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testArtists() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, id.toString())

        // when
        sut(mediaId)

        // then
        verify(artistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyNoMoreInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testGenres() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString())

        // when
        sut(mediaId)

        // then
        verify(genreGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyNoMoreInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }


    @Test
    fun testPodcastPlaylists() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_PLAYLIST, id.toString())

        // when
        sut(mediaId)

        // then
        verify(podcastPlaylistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPodcastArtists() {
        // given
        val id = 1L
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS_AUTHORS, id.toString())

        // when
        sut(mediaId)

        // then
        verify(podcastArtistGateway).observeByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyNoMoreInteractions(podcastArtistGateway)
    }

    @Test
    fun testNotAllowed()  {
        // given
        val allowed = listOf(
            MediaIdCategory.FOLDERS,
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.ALBUMS,
            MediaIdCategory.ARTISTS,
            MediaIdCategory.GENRES,
            MediaIdCategory.PODCASTS_PLAYLIST,
            MediaIdCategory.PODCASTS_AUTHORS
        )

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }
            try {
                val mediaId = MediaId.createCategoryValue(value, "1")

                // when
                sut(mediaId)
                Assert.fail("only $allowed is allow, instead was $value")
            } catch (ex: IllegalArgumentException) {
            }
        }

        // then
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

}