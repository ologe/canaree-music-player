package dev.olog.data.playable

import dev.olog.testing.IndexedTrack
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class PlayablesQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playablesQueries

    @Test
    fun `test selectAll`() {
        blacklistQueries.insert("yes")

        indexedQueries.insert(IndexedTrack("1", is_podcast = false))
        indexedQueries.insert(IndexedTrack("2", is_podcast = false))
        indexedQueries.insert(IndexedTrack("3", is_podcast = true))
        indexedQueries.insert(IndexedTrack("4", is_podcast = true))
        // below should be filtered
        indexedQueries.insert(IndexedTrack("5", is_podcast = false, directory = "yes"))
        indexedQueries.insert(IndexedTrack("6", is_podcast = true, directory = "yes"))

        val actual = queries.selectAll().executeAsList()
        Assert.assertEquals(
            listOf("1", "2", "3", "4"),
            actual.map { it.id }
        )
    }

}