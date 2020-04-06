package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Companion.PODCAST_CATEGORY
import dev.olog.core.MediaId.Companion.SONGS_CATEGORY
import dev.olog.core.MediaIdCategory.ALBUMS
import dev.olog.core.MediaIdCategory.PODCASTS_AUTHORS
import dev.olog.core.Mocks
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class AddToPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway>()
    private val getSongList = mock<GetSongListByParamUseCase>()
    private val sut = AddToPlaylistUseCase(
        playlistGateway, podcastPlaylistGateway, getSongList
    )

    @Test(expected = IllegalArgumentException::class)
    fun `test invoke with different playlist and mediaId types`() = runBlockingTest {
        val playlist = Playlist(1, "", 0, true)
        val mediaId = SONGS_CATEGORY

        sut(playlist, mediaId)
    }

    @Test
    fun testInvokeWithPodcast() = runBlockingTest {
        // given
        val playlistId = 1L
        val podcastId = 10L
        val playlist = Mocks.playlist.copy(id = playlistId, isPodcast = true)
        val mediaId = PODCAST_CATEGORY.playableItem(podcastId)

        // when
        sut(playlist, mediaId)

        // then
        verify(podcastPlaylistGateway).addSongsToPlaylist(playlistId, listOf(podcastId))
        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun testInvokeWithTrack() = runBlockingTest {
        // given
        val playlistId = 1L
        val songId = 10L
        val playlist = Mocks.playlist.copy(id = playlistId, isPodcast = false)
        val mediaId = SONGS_CATEGORY.playableItem(songId)

        // when
        sut(playlist, mediaId)

        // then
        verify(playlistGateway).addSongsToPlaylist(playlistId, listOf(songId))
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun `test invoke with podcast tracklist`() = runBlockingTest {
        // given
        val playlistId = 1L
        val songId = 10L
        val playlist = Mocks.playlist.copy(id = playlistId, isPodcast = true)
        val mediaId = Category(PODCASTS_AUTHORS, 1)
        val song = Mocks.podcast.copy(id = songId)

        whenever(getSongList.invoke(mediaId)).thenReturn(listOf(song))

        // when
        sut(playlist, mediaId)

        // then
        verify(podcastPlaylistGateway).addSongsToPlaylist(playlistId, listOf(songId))
        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(playlistGateway)
        verify(getSongList).invoke(mediaId)
    }

    @Test
    fun `test invoke with song tracklist`() = runBlockingTest {
        // given
        val playlistId = 1L
        val songId = 10L
        val playlist = Mocks.playlist.copy(id = playlistId, isPodcast = false)
        val mediaId = Category(ALBUMS, 1)
        val song = Mocks.song.copy(id = songId)

        whenever(getSongList.invoke(mediaId)).thenReturn(listOf(song))

        // when
        sut(playlist, mediaId)

        // then
        verify(playlistGateway).addSongsToPlaylist(playlistId, listOf(songId))
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
        verify(getSongList).invoke(mediaId)
    }

}