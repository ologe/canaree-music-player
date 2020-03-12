package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val artist = Mocks.artist.copy(id = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.ARTISTS, id),
            artist.getMediaId()
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val artist = Mocks.podcastArtist.copy(id = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.PODCASTS_AUTHORS, id),
            artist.getMediaId()
        )
    }

}