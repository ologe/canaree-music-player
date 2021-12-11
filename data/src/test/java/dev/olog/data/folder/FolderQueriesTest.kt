package dev.olog.data.folder

import dev.olog.data.FolderView
import dev.olog.data.IndexedPlayables
import dev.olog.data.QueriesConstants
import dev.olog.data.QueriesConstants.recentlyAddedMaxTime
import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class FolderQueriesTest {

    private val db = TestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.foldersQueries

    @Before
    fun setup() {
        blacklistQueries.insert("dir")

        indexedQueries.insert(IndexedPlayables(id = 100, directory = "dir1", is_podcast = false))
        indexedQueries.insert(IndexedPlayables(id = 101, directory = "dir1", is_podcast = false))
        indexedQueries.insert(IndexedPlayables(id = 102, directory = "dir1", is_podcast = false))
        indexedQueries.insert(IndexedPlayables(id = 103, directory = "dir2", is_podcast = false))
        indexedQueries.insert(IndexedPlayables(id = 104, directory = "dir2", is_podcast = false))
        // below should be filtered
        indexedQueries.insert(IndexedPlayables(id = 105, directory = "dir", is_podcast = true))
        indexedQueries.insert(IndexedPlayables(id = 106, directory = "dir", is_podcast = false))
    }

    @Test
    fun `test selectAll`() {
        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            FolderView(directory = "dir1", songs = 3),
            FolderView(directory = "dir2", songs = 2),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById`() {
        val actual = queries.selectById(directory = "dir1").executeAsOne()
        val expected = FolderView(directory = "dir1", songs = 3)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById, should be null when not present`() {
        val actual = queries.selectById(directory = "dir5").executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test selectRecentlyAddedSongs`() {
        fun song(songId: Long, dateAdded: Long, directory: String) = IndexedPlayables(
            id = songId,
            collection_id = 0,
            date_added = dateAdded,
            is_podcast = false,
            directory = directory
        )

        indexedQueries.deleteAll()

        val unixTimestamp = QueriesConstants.unitTimestamp()
        indexedQueries.insert(song(songId = 1, directory = "dir10", dateAdded = unixTimestamp))
        indexedQueries.insert(song(songId = 2, directory = "dir10", dateAdded = unixTimestamp - recentlyAddedMaxTime + 1.days.inWholeSeconds))
        indexedQueries.insert(song(songId = 3, directory = "dir10", dateAdded = unixTimestamp - recentlyAddedMaxTime + 3.days.inWholeSeconds))
        indexedQueries.insert(song(songId = 4, directory = "dir20", dateAdded = unixTimestamp - recentlyAddedMaxTime + 2.days.inWholeSeconds))
        // below should be skipped
        indexedQueries.insert(song(songId = 5, directory = "dir10", dateAdded = unixTimestamp - recentlyAddedMaxTime - 1.days.inWholeSeconds))
        indexedQueries.insert(song(songId = 6, directory = "dir", dateAdded = unixTimestamp))

        val actual = queries.selectRecentlyAddedSongs("dir10").executeAsList()
        val expected = listOf(1L, 3L, 2L)
        Assert.assertEquals(expected, actual.map { it.id })
    }

}