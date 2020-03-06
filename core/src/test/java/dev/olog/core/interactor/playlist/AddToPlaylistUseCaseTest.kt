package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AddToPlaylistUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val getSongList = mock<GetSongListByParamUseCase>()
    private val sut = AddToPlaylistUseCase(
        playlistGateway, podcastGateway, getSongList
    )

    @Test(expected = IllegalArgumentException::class)
    fun testInvokeWithWrongPlaylistAndMediaId() = runBlocking {
        val playlist = Playlist(1, "", 0, true)
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.SONGS, "")

        sut(playlist, mediaId)
    }

    @Test
    fun testInvokeWithPodcast() = runBlocking {
        // given
        val playlistId = 1L
        val podcastId = 10L
        val playlist = Playlist(playlistId, "", 0, true)
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS, ""), podcastId
        )

        // when
        sut(playlist, mediaId)

        // then
        verify(podcastGateway).addSongsToPlaylist(playlistId, listOf(podcastId))
        verifyNoMoreInteractions(podcastGateway)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun testInvokeWithTrack() = runBlocking {
        // given
        val playlistId = 1L
        val podcastId = 10L
        val playlist = Playlist(playlistId, "", 0, false)
        val mediaId = MediaId.playableItem(
            MediaId.createCategoryValue(MediaIdCategory.SONGS, ""), podcastId
        )

        // when
        sut(playlist, mediaId)

        // then
        verify(playlistGateway).addSongsToPlaylist(playlistId, listOf(podcastId))
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun testInvokeWithTrackList() = runBlocking {
        // given
        val playlistId = 1L
        val songId = 10L
        val playlist = Playlist(playlistId, "", 0, false)
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "")
        val song = Mocks.song.copy(id = songId)

        whenever(getSongList.invoke(mediaId)).thenReturn(listOf(song))

        // when
        sut(playlist, mediaId)

        // then
        verify(playlistGateway).addSongsToPlaylist(playlistId, listOf(songId))
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
        verify(getSongList).invoke(mediaId)
    }

}