package dev.olog.data

import org.junit.Assert
import org.junit.Test

class PodcastPositionQueriesTest {

    private val db = TestDatabase()
    private val queries = db.podcastPositionQueries

    @Test
    fun test() {
        queries.insert("1", 1000)

        Assert.assertEquals(
            Podcast_position("1", 1000),
            queries.selectById("1").executeAsOne(),
        )
    }

    @Test
    fun `test, should be null`() {
        queries.insert("1", 1000)

        Assert.assertEquals(
            null,
            queries.selectById("2").executeAsOneOrNull(),
        )
    }

}