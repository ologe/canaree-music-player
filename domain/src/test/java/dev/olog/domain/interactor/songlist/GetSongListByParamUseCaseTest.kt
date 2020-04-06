package dev.olog.domain.interactor.songlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import org.junit.Test

class GetSongListByParamUseCaseTest {
    
    private val folderGateway = mock<FolderGateway>()
    private val playlistGateway = mock<PlaylistGateway>()
    private val trackGateway = mock<TrackGateway>()
    private val albumGateway = mock<AlbumGateway>()
    private val artistGateway = mock<ArtistGateway>()
    private val genreGateway = mock<GenreGateway>()
    
    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway>()
    private val podcastArtistGateway = mock<PodcastAuthorGateway>()
    
    private val sut = GetSongListByParamUseCase(
        folderGateway, playlistGateway, trackGateway, albumGateway, artistGateway,
        genreGateway, podcastPlaylistGateway, podcastArtistGateway
    )
    
    @Test
    fun testFolders(){
        // given
        val id = 1L
        val mediaId = Category(FOLDERS, id)
        
        // when
        sut(mediaId)
        
        verify(folderGateway).getTrackListByParam(id)
        verifyNoMoreInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPlaylists(){
        // given
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        // when
        sut(mediaId)

        verify(playlistGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testSongs(){
        // given
        val id = 1L
        val mediaId = Category(SONGS, id)

        // when
        sut(mediaId)

        verify(trackGateway).getAllTracks()
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testAlbums(){
        // given
        val id = 1L
        val mediaId = Category(ALBUMS, id)

        // when
        sut(mediaId)

        verify(albumGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyNoMoreInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testArtists(){
        // given
        val id = 1L
        val mediaId = Category(ARTISTS, id)

        // when
        sut(mediaId)

        verify(artistGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyNoMoreInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testGenre(){
        // given
        val id = 1L
        val mediaId = Category(GENRES, id)

        // when
        sut(mediaId)

        verify(genreGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyNoMoreInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPodcastPlaylists(){
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        sut(mediaId)

        verify(podcastPlaylistGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPodcasts() {
        // given
        val mediaId = PODCAST_CATEGORY

        // when
        sut(mediaId)

        verify(trackGateway).getAllPodcasts()
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(podcastArtistGateway)
    }

    @Test
    fun testPodcastArtist(){
        // given
        val id = 1L
        val mediaId = Category(PODCASTS_AUTHORS, id)

        // when
        sut(mediaId)

        verify(podcastArtistGateway).getTrackListByParam(id)
        verifyZeroInteractions(folderGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(albumGateway)
        verifyZeroInteractions(artistGateway)
        verifyZeroInteractions(genreGateway)

        verifyZeroInteractions(podcastPlaylistGateway)
        verifyNoMoreInteractions(podcastArtistGateway)
    }
    
}