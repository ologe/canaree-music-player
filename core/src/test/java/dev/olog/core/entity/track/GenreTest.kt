package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.Mocks
import org.junit.Assert.*
import org.junit.Test

class GenreTest {

    @Test
    fun testTrackGetMediaId() {
        val id = 1L
        val genre = Mocks.genre.copy(id = id)
        assertEquals(
            MediaId.createCategoryValue(MediaIdCategory.GENRES, id.toString()),
            genre.getMediaId()
        )
    }

}