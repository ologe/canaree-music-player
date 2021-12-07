package dev.olog.data

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayingItemQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playingItemQueries

    companion object {
        private val song = IndexedPlayables(id = 1, is_podcast = false)
        private val podcastEpisodes = IndexedPlayables(id = 2, is_podcast = true)
    }

    @Before
    fun setup() {
        indexedQueries.insert(song)
        indexedQueries.insert(podcastEpisodes)
    }

    @Test
    fun `initial values should be null`() {
        val actual = queries.select().executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as song`() {
        queries.replace(Playing_item(1L))

        val actual = queries.select().executeAsOne()

        Assert.assertEquals(song, actual)
    }

    @Test
    fun `test playing item as podcast episode`() {
        queries.replace(Playing_item(2L))
        val actual = queries.select().executeAsOne()

        Assert.assertEquals(podcastEpisodes, actual)
    }

    @Test
    fun `test playing item as song should be null when blacklisted`() {
        blacklistQueries.insert(Blacklist("yes"))
        indexedQueries.insert(IndexedPlayables(3L, directory = "yes", is_podcast = false))
        queries.replace(Playing_item(3L))

        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as podcast episode should be null when blacklisted`() {
        blacklistQueries.insert(Blacklist("yes"))
        indexedQueries.insert(IndexedPlayables(3L, directory = "yes", is_podcast = true))
        queries.replace(Playing_item(3L))
        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

}