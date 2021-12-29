package dev.olog.data.playing.queue

import dev.olog.testing.IndexedTrack
import dev.olog.data.TestDatabase
import dev.olog.data.playingQueue.SelectAll
import org.junit.Assert
import org.junit.Test

class PlayingQueueQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playingQueueQueries

    @Test
    fun test() {
        blacklistQueries.insert("dir")

        indexedQueries.insert(IndexedTrack("1", is_podcast = false))
        indexedQueries.insert(IndexedTrack("2", is_podcast = false))
        indexedQueries.insert(IndexedTrack("3", is_podcast = true))
        indexedQueries.insert(IndexedTrack("4", is_podcast = true))
        // below should be filtered
        indexedQueries.insert(IndexedTrack("100", is_podcast = false, directory = "dir"))
        indexedQueries.insert(IndexedTrack("101", is_podcast = true, directory = "dir"))

        // insert
        queries.insert(playable_id = "4", play_order = 4)
        queries.insert(playable_id = "2", play_order = 3)
        queries.insert(playable_id = "3", play_order = 2)
        queries.insert(playable_id = "1", play_order = 1)
        queries.insert(playable_id = "100", play_order = 5)
        queries.insert(playable_id = "101", play_order = 6)

        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            1 to "1",
            2 to "3",
            3 to "2",
            4 to "4",
        )
        Assert.assertEquals(expected, actual.map { it.play_order to it.id })

        // delete
        queries.deleteAll()
        Assert.assertEquals(emptyList<SelectAll>(), queries.selectAll().executeAsList())
    }

}