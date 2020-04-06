package dev.olog.domain.entity.track

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.GENRES
import dev.olog.domain.Mocks
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreTest {

    @Test
    fun testGetMediaId() {
        val id = 1L
        val genre = Mocks.genre.copy(id = id)
        assertEquals(
            Category(GENRES, id),
            genre.mediaId
        )
    }

}