package dev.olog.core.interactor.playlist

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
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
        val mediaId = Category(
            PODCASTS_PLAYLIST, id
        )

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
        val mediaId = Category(
            PLAYLISTS, id
        )

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

        for (value in MediaIdCategory.values()) {
            if (value in allowed) {
                continue
            }

            try {
                sut(Category(value, 1), "name")
                fail("invalid value $value")
            } catch (ex: IllegalArgumentException) {

            }
        }
        verifyZeroInteractions(playlistGateway)
        verifyZeroInteractions(podcastGateway)
    }

}