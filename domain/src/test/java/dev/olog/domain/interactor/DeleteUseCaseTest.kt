package dev.olog.domain.interactor

import com.nhaarman.mockitokotlin2.*
import dev.olog.domain.MediaId
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.Mocks
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class DeleteUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastPlaylistGateway = mock<PodcastPlaylistGateway>()
    private val trackGateway = mock<TrackGateway>()
    private val getSongList = mock<GetSongListByParamUseCase>()
    private val sut = DeleteUseCase(
        playlistGateway, podcastPlaylistGateway, trackGateway, getSongList
    )

    @Test
    fun `test delete single`() = runBlockingTest {
        val id = 1L
        val mediaId = MediaId.SONGS_CATEGORY.playableItem(id)

        sut(mediaId)
        verify(trackGateway).deleteSingle(id)
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
        verifyNoMoreInteractions(trackGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun `test delete podcast playlist`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        sut(mediaId)
        verify(podcastPlaylistGateway).deletePlaylist(id)
        verifyZeroInteractions(playlistGateway)
        verifyNoMoreInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun `test delete playlist`() = runBlockingTest {
        val id = 1L
        val mediaId = Category(PLAYLISTS, id)

        sut(mediaId)
        verify(playlistGateway).deletePlaylist(id)
        verifyNoMoreInteractions(playlistGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
        verifyZeroInteractions(trackGateway)
        verifyZeroInteractions(getSongList)
    }

    @Test
    fun `test verity delete group`() = runBlockingTest {
        val notGroup = listOf(
            PLAYLISTS, PODCASTS_PLAYLIST
        )

        for ((index, value) in values().withIndex()) {
            if (value in notGroup) {
                continue
            }
            val mediaId = Category(value, index.toLong())
            val list = listOf(Mocks.song.copy(id = index.toLong()))
            whenever(getSongList.invoke(mediaId)).thenReturn(list)

            sut(mediaId)
            verify(getSongList).invoke(mediaId)
            verify(trackGateway).deleteGroup(listOf(index.toLong()))
        }

        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(podcastPlaylistGateway)
        verifyNoMoreInteractions(trackGateway)
        verifyNoMoreInteractions(getSongList)
    }

}