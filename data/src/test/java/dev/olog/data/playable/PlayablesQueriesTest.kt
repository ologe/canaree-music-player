package dev.olog.data.playable

import dev.olog.testing.IndexedPlayables
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

        indexedQueries.insert(IndexedPlayables(1, is_podcast = false))
        indexedQueries.insert(IndexedPlayables(2, is_podcast = false))
        indexedQueries.insert(IndexedPlayables(3, is_podcast = true))
        indexedQueries.insert(IndexedPlayables(4, is_podcast = true))
        // below should be filtered
        indexedQueries.insert(IndexedPlayables(5, is_podcast = false, directory = "yes"))
        indexedQueries.insert(IndexedPlayables(6, is_podcast = true, directory = "yes"))

        val actual = queries.selectAll().executeAsList()
        Assert.assertEquals(
            listOf(1L, 2L, 3L, 4L),
            actual.map { it.id }
        )
    }

}