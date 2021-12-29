package dev.olog.data.history

import dev.olog.data.TestDatabase
import dev.olog.data.index.Indexed_playables
import dev.olog.testing.IndexedTrack
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
        blacklistQueries.insert("yes")

        val data = listOf(
            IndexedTrack(id = "1", is_podcast = false),
            IndexedTrack(id = "2", is_podcast = false),
            IndexedTrack(id = "3", is_podcast = false),
            IndexedTrack(id = "4", is_podcast = false, directory = "yes"),
            IndexedTrack(id = "5", is_podcast = true),
            IndexedTrack(id = "6", is_podcast = true),
            IndexedTrack(id = "7", is_podcast = true),
            IndexedTrack(id = "8", is_podcast = true, directory = "yes"),
        )
        data.forEach { indexedQueries.insert(it) }
    }

    @Test
    fun `test selectAllSongs`() {
        Assert.assertEquals(emptyList<Indexed_playables>(), queries.selectAllSongs().executeAsList())

        queries.insert("1", 50)
        queries.insert("3", 100)
        queries.insert("4", 200)  // should be filtered

        Assert.assertEquals(
            listOf(
                IndexedTrack(id = "3", is_podcast = false),
                IndexedTrack(id = "1", is_podcast = false),
            ),
            queries.selectAllSongs().executeAsList()
        )
    }

    @Test
    fun `test selectAllPodcastEpisodes`() {
        Assert.assertEquals(emptyList<Indexed_playables>(), queries.selectAllPodcastEpisodes().executeAsList())

        queries.insert("5", 50)
        queries.insert("6", 100)
        queries.insert("8", 200) // should be filtered

        Assert.assertEquals(
            listOf(
                IndexedTrack(id = "6", is_podcast = true),
                IndexedTrack(id = "5", is_podcast = true),
            ),
            queries.selectAllPodcastEpisodes().executeAsList()
        )
    }

}