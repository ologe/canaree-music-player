package dev.olog.data

import dev.olog.data.repository.replace
import dev.olog.testing.IndexedTrack
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayingItemQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playingItemQueries

    companion object {
        private val song = IndexedTrack(id = "1", is_podcast = false)
        private val podcastEpisode = IndexedTrack(id = "2", is_podcast = true)
    }

    @Before
    fun setup() {
        indexedQueries.insert(song)
        indexedQueries.insert(podcastEpisode)
    }

    @Test
    fun `initial values should be null`() {
        val actual = queries.select().executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as song`() {
        queries.replace(id = "1")

        val actual = queries.select().executeAsOne()

        Assert.assertEquals(song, actual)
    }

    @Test
    fun `test playing item as podcast episode`() {
        queries.replace(id = "2")
        val actual = queries.select().executeAsOne()

        Assert.assertEquals(podcastEpisode, actual)
    }

    @Test
    fun `test playing item as song should be null when blacklisted`() {
        blacklistQueries.insert("yes")
        indexedQueries.insert(IndexedTrack("3", directory = "yes", is_podcast = false))
        queries.replace(id = "3")

        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test playing item as podcast episode should be null when blacklisted`() {
        blacklistQueries.insert("yes")
        indexedQueries.insert(IndexedTrack("3", directory = "yes", is_podcast = true))
        queries.replace(id = "3")
        val actual = queries.select().executeAsOneOrNull()

        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test replace, should be only one item`() {
        queries.replace("1")
        queries.replace("2")

        val actual = queries.select().executeAsOne()

        Assert.assertEquals(podcastEpisode, actual)
    }

}