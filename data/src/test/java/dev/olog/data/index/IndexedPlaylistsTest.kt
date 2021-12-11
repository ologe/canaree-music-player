package dev.olog.data.index

import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class IndexedPlaylistsTest {

    private val db = TestDatabase()
    private val queries = db.indexedPlaylistsQueries

    companion object {
        private val autoPlaylists = listOf(
            Indexed_playlists(-1, "last_added"),
            Indexed_playlists(-2, "favorites"),
            Indexed_playlists(-3, "history"),
            Indexed_playlists(-4, "podcast_last_added"),
            Indexed_playlists(-5, "podcast_favorites"),
            Indexed_playlists(-6, "podcast_history"),
        ).sortedBy { it.id }
    }

    @Test
    fun `test insert`() {
        val item1 = Indexed_playlists(id = 1, title = "playlist1")
        val item2 = Indexed_playlists(id = 2, title = "playlist2")

        // when
        queries.insert(item1)
        queries.insert(item2)

        // then
        Assert.assertEquals(
            autoPlaylists + listOf(item1, item2),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insert fail on same id`() {
        var exception: Throwable? = null
        try {
            val item1 = Indexed_playlists(id = 1, title = "playlist1")
            val item2 = Indexed_playlists(id = 1, title = "playlist2")

            // when
            queries.insert(item1)
            queries.insert(item2)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_playlists.id)", exception.message)
    }

    @Test
    fun `test delete`() {
        val item1 = Indexed_playlists(id = 1, title = "playlist1")
        val item2 = Indexed_playlists(id = 2, title = "playlist2")
        queries.insert(item1)
        queries.insert(item2)

        // when
        queries.delete(id = 1)

        // then
        Assert.assertEquals(
            autoPlaylists + listOf(item2),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test delete fail on auto playlists`() {
        Assert.assertEquals(
            autoPlaylists,
            queries.selectAll().executeAsList()
        )
        // when
        for (playlist in autoPlaylists) {
            queries.delete(playlist.id)
        }

        // then
        Assert.assertEquals(
            autoPlaylists,
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test deleteAll`() {
        val item1 = Indexed_playlists(id = 1, title = "playlist1")
        val item2 = Indexed_playlists(id = 2, title = "playlist2")
        queries.insert(item1)
        queries.insert(item2)

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            autoPlaylists,
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test deleteAll, fail on auto playlists`() {
        Assert.assertEquals(
            autoPlaylists,
            queries.selectAll().executeAsList()
        )

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            autoPlaylists,
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insertPlayable`() {
        val item1 = Indexed_playlists_playables(1, 10, 100)
        val item2 = Indexed_playlists_playables(2, 20, 200)

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
            val item1 = Indexed_playlists_playables(1, 10, 100)
            val item2 = Indexed_playlists_playables(1, 10, 200)

            // when
            queries.insertPlayable(item1)
            queries.insertPlayable(item2)
        } catch (ex: Throwable) {
            exception = ex
        }
        requireNotNull(exception)
        Assert.assertEquals("[SQLITE_CONSTRAINT_PRIMARYKEY]  A PRIMARY KEY constraint failed (UNIQUE constraint failed: indexed_playlists_playables.playlist_id, indexed_playlists_playables.playable_id)", exception.message)
    }

    @Test
    fun `test deletePlayable`() {
        val item1 = Indexed_playlists_playables(1, 10, 100)
        val item2 = Indexed_playlists_playables(2, 20, 200)
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // when
        queries.deletePlayable(1, 10)

        // then
        Assert.assertEquals(
            listOf(item2),
            queries.selectAllPlayables().executeAsList()
        )
    }

    @Test
    fun `test deletePlayableAll`() {
        val item1 = Indexed_playlists_playables(1, 10, 100)
        val item2 = Indexed_playlists_playables(2, 20, 200)
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // when
        queries.deleteAllPlayables()

        // then
        Assert.assertEquals(
            emptyList<Indexed_playlists_playables>(),
            queries.selectAllPlayables().executeAsList()
        )
    }

}