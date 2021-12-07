package dev.olog.data

import dev.olog.data.index.Indexed_playables
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HistoryQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.historyQueries

    @Before
    fun setup() {
        blacklistQueries.insert(Blacklist("yes"))

        val data = listOf(
            IndexedPlayables(id = 1, is_podcast = false),
            IndexedPlayables(id = 2, is_podcast = false),
            IndexedPlayables(id = 3, is_podcast = false),
            IndexedPlayables(id = 4, is_podcast = false, directory = "yes"),
            IndexedPlayables(id = 5, is_podcast = true),
            IndexedPlayables(id = 6, is_podcast = true),
            IndexedPlayables(id = 7, is_podcast = true),
            IndexedPlayables(id = 8, is_podcast = true, directory = "yes"),
        )
        data.forEach { indexedQueries.insert(it) }
    }

    @Test
    fun `test selectAllSongs`() {
        Assert.assertEquals(emptyList<Indexed_playables>(), queries.selectAllSongs().executeAsList())

        queries.insert(History(1, 50))
        queries.insert(History(3, 100))
        queries.insert(History(4, 200))  // should be filtered

        Assert.assertEquals(
            listOf(
                IndexedPlayables(id = 3, is_podcast = false),
                IndexedPlayables(id = 1, is_podcast = false),
            ),
            queries.selectAllSongs().executeAsList()
        )
    }

    @Test
    fun `test selectAllPodcastEpisodes`() {
        Assert.assertEquals(emptyList<Indexed_playables>(), queries.selectAllPodcastEpisodes().executeAsList())

        queries.insert(History(5, 50))
        queries.insert(History(6, 100))
        queries.insert(History(8, 200)) // should be filtered

        Assert.assertEquals(
            listOf(
                IndexedPlayables(id = 6, is_podcast = true),
                IndexedPlayables(id = 5, is_podcast = true),
            ),
            queries.selectAllPodcastEpisodes().executeAsList()
        )
    }

}