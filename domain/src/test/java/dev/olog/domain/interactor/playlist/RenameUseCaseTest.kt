package dev.olog.domain.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.MediaIdCategory.PLAYLISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.domain.catchIaeOnly
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class RenameUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RenameUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlockingTest {
        // given
        val id = 1L
        val newTitle = "new title"
        val mediaId = Category(PODCASTS_PLAYLIST, id)

        // when
        sut(mediaId, newTitle)

        verify(podcastGateway).renamePlaylist(id, newTitle)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlockingTest {
        // given
        val id = 1L
        val newTitle = "new title"
        val mediaId = Category(PLAYLISTS, id)

        // when
        sut(mediaId, newTitle)

        verify(playlistGateway).renamePlaylist(id, newTitle)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokeAuto() = runBlockingTest {
        val allowed = listOf(
            PLAYLISTS, PODCASTS_PLAYLIST
        )

        MediaIdCategory.values().catchIaeOnly(allowed) { value ->
            sut(Category(value, 1), "name")
        }

        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
    }

}