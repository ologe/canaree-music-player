package dev.olog.data

import org.junit.Assert
import org.junit.Test

class LyricsQueriesTest {

    private val db = TestDatabase()
    private val queries = db.lyricsQueries

    @Test
    fun `test select`() {
        queries.insert(1, "text")

        val actual = queries.selectById(1).executeAsOne()
        val expected = Lyrics(1, "text")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test select missing item`() {
        queries.insert(1, "text")

        val actual = queries.selectById(2).executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

}