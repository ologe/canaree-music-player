package dev.olog.data

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
            AndroidIndexedPlayables(id = 1, title = "", is_podcast = false, date_added = 50),
            AndroidIndexedPlayables(id = 2, title = "áa", is_podcast = false, date_added = 100),
            AndroidIndexedPlayables(id = 3, title = "ab", is_podcast = false, date_added = 100),
            AndroidIndexedPlayables(id = 4, title = "", is_podcast = false, date_added = 100, directory = "yes"), // should be filtered
            AndroidIndexedPlayables(id = 5, title = "", is_podcast = true, date_added = 70),
            AndroidIndexedPlayables(id = 6, title = "êa", is_podcast = true, date_added = 200),
            AndroidIndexedPlayables(id = 7, title = "eb", is_podcast = true, date_added = 200),
            AndroidIndexedPlayables(id = 8, title = "", is_podcast = true, date_added = 300, directory = "yes"), // should be filtered
        )
        data.forEach { indexedQueries.insert(it) }
    }

    @Test
    fun testSelectAllSongs() {
        val actual = queries.selectAllSongs().executeAsList()
        val expected = listOf(
            AndroidIndexedPlayables(id = 2, title = "áa", is_podcast = false, date_added = 100),
            AndroidIndexedPlayables(id = 3, title = "ab", is_podcast = false, date_added = 100),
            AndroidIndexedPlayables(id = 1, title = "", is_podcast = false, date_added = 50),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testSelectAllPodcastEpisodes() {
        val actual = queries.selectAllPodcastEpisodes().executeAsList()
        val expected = listOf(
            AndroidIndexedPlayables(id = 6, title = "êa", is_podcast = true, date_added = 200),
            AndroidIndexedPlayables(id = 7, title = "eb", is_podcast = true, date_added = 200),
            AndroidIndexedPlayables(id = 5, title = "", is_podcast = true, date_added = 70),
        )
        Assert.assertEquals(expected, actual)
    }

}