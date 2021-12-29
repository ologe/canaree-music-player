package dev.olog.data

import dev.olog.testing.IndexedTrack
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LastAddedQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.lastAddedQueries

    @Before
    fun setup() {
        blacklistQueries.insert("yes")
        
        val data = listOf(
            IndexedTrack(id = "1", title = "", is_podcast = false, date_added = 50),
            IndexedTrack(id = "2", title = "áa", is_podcast = false, date_added = 100),
            IndexedTrack(id = "3", title = "ab", is_podcast = false, date_added = 100),
            IndexedTrack(id = "4", title = "", is_podcast = false, date_added = 100, directory = "yes"), // should be filtered
            IndexedTrack(id = "5", title = "", is_podcast = true, date_added = 70),
            IndexedTrack(id = "6", title = "êa", is_podcast = true, date_added = 200),
            IndexedTrack(id = "7", title = "eb", is_podcast = true, date_added = 200),
            IndexedTrack(id = "8", title = "", is_podcast = true, date_added = 300, directory = "yes"), // should be filtered
        )
        data.forEach { indexedQueries.insert(it) }
    }

    @Test
    fun testSelectAllSongs() {
        val actual = queries.selectAllSongs().executeAsList()
        val expected = listOf(
            IndexedTrack(id = "2", title = "áa", is_podcast = false, date_added = 100),
            IndexedTrack(id = "3", title = "ab", is_podcast = false, date_added = 100),
            IndexedTrack(id = "1", title = "", is_podcast = false, date_added = 50),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testSelectAllPodcastEpisodes() {
        val actual = queries.selectAllPodcastEpisodes().executeAsList()
        val expected = listOf(
            IndexedTrack(id = "6", title = "êa", is_podcast = true, date_added = 200),
            IndexedTrack(id = "7", title = "eb", is_podcast = true, date_added = 200),
            IndexedTrack(id = "5", title = "", is_podcast = true, date_added = 70),
        )
        Assert.assertEquals(expected, actual)
    }

}