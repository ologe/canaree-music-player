package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.ARTISTS
import dev.olog.domain.MediaIdCategory.PODCASTS_AUTHORS
import dev.olog.domain.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistTest {

    @Test
    fun testGetMediaId() {
        val id = 1L
        val artist = Mocks.artist.copy(id = id)
        assertEquals(
            Category(ARTISTS, id),
            artist.mediaId
        )
    }

    @Test
    fun testPodcastGetMediaId() {
        val id = 1L
        val artist = Mocks.podcastAuthor.copy(id = id)
        assertEquals(
            Category(PODCASTS_AUTHORS, id),
            artist.mediaId
        )
    }

}