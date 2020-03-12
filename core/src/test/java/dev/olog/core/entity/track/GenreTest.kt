package dev.olog.core.entity.track

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.GENRES
import dev.olog.core.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val genre = Mocks.genre.copy(id = id)
        assertEquals(
            Category(GENRES, id),
            genre.getMediaId()
        )
    }

}