package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import org.junit.Assert.*
import org.junit.Test

class PlaylistTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val playlist = Mocks.playlist.copy(id = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, id.toString()),
            playlist.getMediaId()
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val playlist = Mocks.podcastPlaylist.copy(id = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS_PLAYLIST, id.toString()),
            playlist.getMediaId()
        )
    }

}