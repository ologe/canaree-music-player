package dev.olog.data.index

import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class IndexedGenresTest {

    private val db = TestDatabase()
    private val queries = db.indexedGenresQueries


    @Test
    fun `test insert`() {
        val item1 = Indexed_genres(id = "1", name = "genre1")
        val item2 = Indexed_genres(id = "2", name = "genre2")

        // when
        queries.insert(item1)
        queries.insert(item2)

        // then
        Assert.assertEquals(
            listOf(item1, item2),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insert fail on same id`() {
        var exception: Throwable? = null
        try {
            val item1 = Indexed_genres(id = "1", name = "genre1")
            val item2 = Indexed_genres(id = "1", name = "genre2")

            // when
            queries.insert(item1)
            queries.insert(item2)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_genres.id)", exception.message)
    }

    @Test
    fun `test delete`() {
        val item1 = Indexed_genres(id = "1", name = "genre1")
        val item2 = Indexed_genres(id = "2", name = "genre2")
        queries.insert(item1)
        queries.insert(item2)

        // when
        queries.delete(id = "1")

        // then
        Assert.assertEquals(
            listOf(item2),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test deleteAll`() {
        val item1 = Indexed_genres(id = "1", name = "genre1")
        val item2 = Indexed_genres(id = "2", name = "genre2")
        queries.insert(item1)
        queries.insert(item2)

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            emptyList<Indexed_playables>(),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insertPlayable`() {
        val item1 = Indexed_genres_playables("1", "10")
        val item2 = Indexed_genres_playables("2", "20")

        // when
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // then
        Assert.assertEquals(
            listOf(item1, item2),
            queries.selectAllPlayables().executeAsList()
        )
    }

    @Test
    fun `test insertPlayable fail on same primary key`() {
        var exception: Throwable? = null
        try {
            val item1 = Indexed_genres_playables("1", "10")
            val item2 = Indexed_genres_playables("1", "10")

            // when
            queries.insertPlayable(item1)
            queries.insertPlayable(item2)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_genres_playables.genre_id, indexed_genres_playables.song_id)", exception.message)
    }

    @Test
    fun `test deletePlayable`() {
        val item1 = Indexed_genres_playables("1", "10")
        val item2 = Indexed_genres_playables("2", "20")
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // when
        queries.deletePlayable("1", "10")

        // then
        Assert.assertEquals(
            listOf(item2),
            queries.selectAllPlayables().executeAsList()
        )
    }

    @Test
    fun `test deletePlayableAll`() {
        val item1 = Indexed_genres_playables("1", "10")
        val item2 = Indexed_genres_playables("2", "20")
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            emptyList<Indexed_genres_playables>(),
            queries.selectAll().executeAsList()
        )
    }

}