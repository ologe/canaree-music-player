package dev.olog.data.playable

import dev.olog.data.Blacklist
import dev.olog.data.IndexedPlayables
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

// selectAllSorted is tested in android test because of collation
class PodcastEpisodesQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.podcastEpisodesQueries

    @Test
    fun `test selectAll, should return non blacklisted and non podcast`() {
        blacklistQueries.insert("yes")

        val expected = listOf(
            IndexedPlayables(id = 1, is_podcast = true, directory = "no"),
            IndexedPlayables(id = 2, is_podcast = true, directory = "no")
        )
        indexedQueries.insert(expected[0])
        indexedQueries.insert(expected[1])
        indexedQueries.insert(IndexedPlayables(id = 1000, is_podcast = true, directory = "yes"))
        indexedQueries.insert(IndexedPlayables(id = 1001, is_podcast = false, directory = "no"))
        indexedQueries.insert(IndexedPlayables(id = 1002, is_podcast = false, directory = "yes"))

        // when
        val actual = queries.selectAll().executeAsList()

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById`() {
        val song = IndexedPlayables(id = 1, is_podcast = true)
        indexedQueries.insert(song)

        // when
        val actual = queries.selectById(id = 1).executeAsOne()

        // then
        Assert.assertEquals(song, actual)
    }

    @Test
    fun `test selectById, should be null when item is missing`() {
        val song = IndexedPlayables(id = 1, is_podcast = true)
        indexedQueries.insert(song)

        // when
        val actual = queries.selectById(id = 2).executeAsOneOrNull()

        // then
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test selectByCollectionId`() {
        val song = IndexedPlayables(id = 1, collection_id = 2, is_podcast = true)
        indexedQueries.insert(song)

        // when
        val actual = queries.selectByCollectionId(collection_id = 2).executeAsOne()

        // then
        Assert.assertEquals(song, actual)
    }

    @Test
    fun `test selectByCollectionId, should be null when item is missing`() {
        val song = IndexedPlayables(id = 1, collection_id = 3, is_podcast = true)
        indexedQueries.insert(song)

        // when
        val actual = queries.selectByCollectionId(collection_id = 2).executeAsOneOrNull()

        // then
        Assert.assertEquals(null, actual)
    }

}