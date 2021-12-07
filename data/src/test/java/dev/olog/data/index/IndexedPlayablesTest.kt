package dev.olog.data.index

import dev.olog.data.IndexedPlayables
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class IndexedPlayablesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries


    @Test
    fun `test insertOne`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)

        // when
        indexedQueries.insert(song)
        indexedQueries.insert(podcast)

        // then
        Assert.assertEquals(
            listOf(song, podcast),
            indexedQueries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insertOne fail on same id`() {
        var exception: Throwable? = null
        try {
            val song = IndexedPlayables(id = 1, is_podcast = false)
            val podcast = IndexedPlayables(id = 1, is_podcast = true)

            // when
            indexedQueries.insert(song)
            indexedQueries.insert(podcast)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_playables.id)", exception.message)
    }

    @Test
    fun `test deleteOne`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)
        indexedQueries.insert(song)
        indexedQueries.insert(podcast)

        // when
        indexedQueries.deleteOne(id = 1)

        // then
        Assert.assertEquals(
            listOf(podcast),
            indexedQueries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test deleteAll`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)
        indexedQueries.insert(song)
        indexedQueries.insert(podcast)

        // when
        indexedQueries.deleteAll()

        // then
        Assert.assertEquals(
            emptyList<Indexed_playables>(),
            indexedQueries.selectAll().executeAsList()
        )
    }

}