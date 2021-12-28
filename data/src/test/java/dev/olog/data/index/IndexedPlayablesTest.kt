package dev.olog.data.index

import dev.olog.testing.IndexedPlayables
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class IndexedPlayablesTest {

    private val db = TestDatabase()
    private val queries = db.indexedPlayablesQueries


    @Test
    fun `test insert`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)

        // when
        queries.insert(song)
        queries.insert(podcast)

        // then
        Assert.assertEquals(
            listOf(song, podcast),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insert fail on same id`() {
        var exception: Throwable? = null
        try {
            val song = IndexedPlayables(id = 1, is_podcast = false)
            val podcast = IndexedPlayables(id = 1, is_podcast = true)

            // when
            queries.insert(song)
            queries.insert(podcast)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_playables.id)", exception.message)
    }

    @Test
    fun `test delete`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)
        queries.insert(song)
        queries.insert(podcast)

        // when
        queries.delete(id = 1)

        // then
        Assert.assertEquals(
            listOf(podcast),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test deleteAll`() {
        val song = IndexedPlayables(id = 1, is_podcast = false)
        val podcast = IndexedPlayables(id = 2, is_podcast = true)
        queries.insert(song)
        queries.insert(podcast)

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            emptyList<Indexed_playables>(),
            queries.selectAll().executeAsList()
        )
    }

}