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

class RemoveDuplicatesUseCaseTest {

    private val playlistGateway = mock<PlaylistGateway>()
    private val podcastGateway = mock<PodcastPlaylistGateway>()
    private val sut = RemoveDuplicatesUseCase(playlistGateway, podcastGateway)

    @Test
    fun testInvokePodcast() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(
            PODCASTS_PLAYLIST, id
        )

        // when
        sut(mediaId)

        verify(podcastGateway).removeDuplicated(id)
        verifyZeroInteractions(playlistGateway)
    }

    @Test
    fun testInvokeTrack() = runBlockingTest {
        // given
        val id = 1L
        val mediaId = Category(
            PLAYLISTS, id
        )

        // when
        sut(mediaId)

        verify(playlistGateway).removeDuplicated(id)
        verifyZeroInteractions(podcastGateway)
    }

    @Test
    fun testInvokeOther() = runBlockingTest {
        // given
        val allowed = listOf(
            PODCASTS_PLAYLIST, PLAYLISTS
        )

        MediaIdCategory.values().catchIaeOnly(allowed) { value ->
            sut(Category(value, 1))
        }

        verifyZeroInteractions(podcastGateway)
        verifyZeroInteractions(playlistGateway)
    }

}