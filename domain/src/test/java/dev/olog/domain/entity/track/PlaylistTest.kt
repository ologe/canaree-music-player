package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.PLAYLISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.domain.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaylistTest {

    @Test
    fun testGetMediaId() {
        val id = 1L
        val playlist = Mocks.playlist.copy(id = id)
        assertEquals(
            Category(PLAYLISTS, id),
            playlist.mediaId
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val playlist = Mocks.podcastPlaylist.copy(id = id)
        assertEquals(
            Category(PODCASTS_PLAYLIST, id),
            playlist.mediaId
        )
    }

}