package dev.olog.data.playable

import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.Blacklist
import dev.olog.data.insertGroup
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SortedPodcastEpisodesQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.podcastEpisodesQueries

    @Before
    fun setup() {
        blacklistQueries.insert(Blacklist("yes"))
        // item to be filtered, blacklisted and non podcast
        indexedQueries.insert(AndroidIndexedPlayables(id = 1000, is_podcast = true, directory = "yes"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1001, is_podcast = false, directory = "no"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1002, is_podcast = false, directory = "yes"))

        // insert data
        val data = listOf(
            AndroidIndexedPlayables(
                id = 1,
                title = "zzz",
                is_podcast = true,
            ),
            AndroidIndexedPlayables(
                id = 2,
                title = "âspace",
                is_podcast = true,
            ),
            AndroidIndexedPlayables(
                id = 3,
                title = "ãtitle",
                is_podcast = true,
            ),
            AndroidIndexedPlayables(
                id = 4,
                title = "middle",
                is_podcast = true,
            ),
        )
        indexedQueries.insertGroup(data)
    }

    @Test
    fun testSelectAllShouldReturnNonBlacklistedAndPodcast() {
        // ignore accents
        val expected = listOf(
            // ignore accents
            "âspace",
            "ãtitle",
            "middle",
            "zzz",
        )

        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.title })
    }

    // TODO test flow updates on blacklist

}