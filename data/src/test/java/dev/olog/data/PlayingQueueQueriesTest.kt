package dev.olog.data

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

        indexedQueries.insert(IndexedPlayables(1L, is_podcast = false))
        indexedQueries.insert(IndexedPlayables(2L, is_podcast = false))
        indexedQueries.insert(IndexedPlayables(3L, is_podcast = true))
        indexedQueries.insert(IndexedPlayables(4L, is_podcast = true))
        // below should be filtered
        indexedQueries.insert(IndexedPlayables(100L, is_podcast = false, directory = "dir"))
        indexedQueries.insert(IndexedPlayables(101L, is_podcast = true, directory = "dir"))

        // insert
        queries.insert(playable_id = 4L, play_order = 4)
        queries.insert(playable_id = 2L, play_order = 3)
        queries.insert(playable_id = 3L, play_order = 2)
        queries.insert(playable_id = 1L, play_order = 1)
        queries.insert(playable_id = 100L, play_order = 5)
        queries.insert(playable_id = 101L, play_order = 6)

        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            1 to 1L,
            2 to 3L,
            3 to 2L,
            4 to 4L,
        )
        Assert.assertEquals(expected, actual.map { it.play_order to it.id })

        // delete
        queries.deleteAll()
        Assert.assertEquals(emptyList<SelectAll>(), queries.selectAll().executeAsList())
    }

}