package dev.olog.data

import org.junit.Assert
import org.junit.Test

class LyricsQueriesTest {

    private val db = TestDatabase()
    private val queries = db.lyricsQueries

    @Test
    fun `test select`() {
        queries.insertText("1", "text")

        val actual = queries.selectById("1").executeAsOne()
        val expected = Lyrics("1", "text", 0)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test select missing item`() {
        queries.insertText("1", "text")

        val actual = queries.selectById("2").executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test replace, start with text`() {
        // insert text
        queries.insertText("1", "text")

        Assert.assertEquals(
            Lyrics("1", "text", 0),
            queries.selectById("1").executeAsOne()
        )

        // replace text
        queries.insertText("1", "abc")

        Assert.assertEquals(
            Lyrics("1", "abc", 0),
            queries.selectById("1").executeAsOne()
        )

        // insert sync
        queries.insertSync("1", 1000)

        Assert.assertEquals(
            Lyrics("1", "abc", 1000),
            queries.selectById("1").executeAsOne()
        )

        // replace sync
        queries.insertSync("1", 500)

        Assert.assertEquals(
            Lyrics("1", "abc", 500),
            queries.selectById("1").executeAsOne()
        )
    }

    @Test
    fun `test replace, start with sync`() {
        // insert sync
        queries.insertSync("1", 1000)

        Assert.assertEquals(
            Lyrics("1", "", 1000),
            queries.selectById("1").executeAsOne()
        )

        // replace sync
        queries.insertSync("1", 500)

        Assert.assertEquals(
            Lyrics("1", "", 500),
            queries.selectById("1").executeAsOne()
        )

        // insert first item text
        queries.insertText("1", "text")

        Assert.assertEquals(
            Lyrics("1", "text", 500),
            queries.selectById("1").executeAsOne()
        )

        // replace text
        queries.insertText("1", "abc")

        Assert.assertEquals(
            Lyrics("1", "abc", 500),
            queries.selectById("1").executeAsOne()
        )
    }

    @Test
    fun `test initDefault, success when not already present`() {
        queries.initDefault("1")

        Assert.assertEquals(
            Lyrics("1", "", 0),
            queries.selectById("1").executeAsOne()
        )
    }

    @Test
    fun `test initDefault, fail when already present`() {
        queries.insertText("1", "abc")

        queries.initDefault("1")

        Assert.assertEquals(
            Lyrics("1", "abc", 0),
            queries.selectById("1").executeAsOne()
        )
    }

}