package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.PLAYLISTS
import dev.olog.core.MediaIdCategory.PODCASTS_PLAYLIST
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaylistTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val playlist = Mocks.playlist.copy(id = id)
        assertEquals(
            Category(PLAYLISTS, id),
            playlist.getMediaId()
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val playlist = Mocks.podcastPlaylist.copy(id = id)
        assertEquals(
            Category(PODCASTS_PLAYLIST, id),
            playlist.getMediaId()
        )
    }

}