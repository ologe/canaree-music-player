package dev.olog.data.index

import dev.olog.data.TestDatabase
import dev.olog.testing.IndexedPlaylistTracks
import org.junit.Assert
import org.junit.Test

class IndexedPlaylistsTest {

    private val db = TestDatabase()
    private val queries = db.indexedPlaylistsQueries

    @Test
    fun `test insert`() {
        val item1 = Indexed_playlists(id = "1", title = "playlist1", path = "path 1")
        val item2 = Indexed_playlists(id = "2", title = "playlist2", path = "path 2")

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
            val item1 = Indexed_playlists(id = "1", title = "playlist1", path = "path 1")
            val item2 = Indexed_playlists(id = "1", title = "playlist2", path = "path 2")

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
        val item1 = Indexed_playlists(id = "1", title = "playlist1", path = "path 1")
        val item2 = Indexed_playlists(id = "2", title = "playlist2", path = "path 2")
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
        val item1 = Indexed_playlists(id = "1", title = "playlist1", path = "path 1")
        val item2 = Indexed_playlists(id = "2", title = "playlist2", path = "path 2")
        queries.insert(item1)
        queries.insert(item2)

        // when
        queries.deleteAll()

        // then
        Assert.assertEquals(
            emptyList<Indexed_playlists>(),
            queries.selectAll().executeAsList()
        )
    }

    @Test
    fun `test insertPlayable`() {
        val item1 = IndexedPlaylistTracks("1", "10", "100", 1000)
        val item2 = IndexedPlaylistTracks("2", "20", "200", 2000)

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
    fun `test deletePlayable`() {
        val item1 = IndexedPlaylistTracks(id = "1", playlistId = "10", playableId = "100", playOrder = 1000)
        val item2 = IndexedPlaylistTracks(id = "2", playlistId = "20", playableId = "200", playOrder = 2000)
        queries.insertPlayable(item1)
        queries.insertPlayable(item2)

        // when
        queries.deletePlayable("10", "100")

        // then
        Assert.assertEquals(
            listOf(item2),
            queries.selectAllPlayables().executeAsList()
        )
    }

    @Test
    fun `test deletePlayableAll`() {
        val item1 = IndexedPlaylistTracks(id = "1", playlistId = "10", playableId = "100", playOrder = 1000)
        val item2 = IndexedPlaylistTracks(id = "2", playlistId = "20", playableId = "200", playOrder = 2000)
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