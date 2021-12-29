package dev.olog.data.collection

import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.IndexedTrack
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Test

class SortedAlbumQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.albumsQueries

    @Test
    fun testSelectAllSortedByTitle() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            IndexedTrack(id = "1", collection_id = "1", collection = "abc", author = "abc2", is_podcast = false),
            IndexedTrack(id = "2", collection_id = "1", collection = "abc", author = "abc2", is_podcast = false),
            IndexedTrack(id = "3", collection_id = "2", collection = "äaa", author = "äaa2", is_podcast = false),
            IndexedTrack(id = "4", collection_id = "3", collection = "<unknown>", author = "<unknown>", is_podcast = false),
            IndexedTrack(id = "5", collection_id = "4", collection = "zzz", author = "zzz2", is_podcast = false),
            // filtered
            IndexedTrack(id = "100", collection_id = "1000", is_podcast = false, directory = "dir"),
        )

        indexedQueries.insertGroup(items)

        // test ascending
        sortQueries.setAlbumsSort(Sort(CollectionSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expectedAsc = listOf(
            "äaa",
            "abc",
            "zzz",
            "<unknown>"
        )
        Assert.assertEquals(expectedAsc, actualAsc.map { it.title })

        // test descending
        sortQueries.setAlbumsSort(Sort(CollectionSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        val expectedDesc = listOf(
            "zzz",
            "abc",
            "äaa",
            "<unknown>"
        )
        Assert.assertEquals(expectedDesc, actualDesc.map { it.title })
    }

    @Test
    fun testSelectAllSortedByAuthor() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            IndexedTrack(id = "1", collection_id = "1", collection = "abc", author = "abc2", is_podcast = false),
            IndexedTrack(id = "2", collection_id = "1", collection = "abc", author = "abc2", is_podcast = false),
            IndexedTrack(id = "3", collection_id = "2", collection = "äaa", author = "äaa2", is_podcast = false),
            IndexedTrack(id = "4", collection_id = "3", collection = "<unknown>", author = "<unknown>", is_podcast = false),
            IndexedTrack(id = "5", collection_id = "4", collection = "zzz", author = "zzz2", is_podcast = false),
            // filtered
            IndexedTrack(id = "100", collection_id = "1000", is_podcast = false, directory = "dir"),
        )

        indexedQueries.insertGroup(items)

        // test ascending
        sortQueries.setAlbumsSort(Sort(CollectionSort.Author, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expectedAsc = listOf(
            "äaa2",
            "abc2",
            "zzz2",
            "<unknown>"
        )
        Assert.assertEquals(expectedAsc, actualAsc.map { it.author })

        // test descending
        sortQueries.setAlbumsSort(Sort(CollectionSort.Author, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        val expectedDesc = listOf(
            "zzz2",
            "abc2",
            "äaa2",
            "<unknown>"
        )
        Assert.assertEquals(expectedDesc, actualDesc.map { it.author })
    }

    @Test
    fun testSelectArtistAlbums() {
        val items = listOf(
            IndexedTrack(id = "10000", is_podcast = false, author_id = "123", collection_id = "1", collection = "abc"),
            IndexedTrack(id = "10001", is_podcast = false, author_id = "123", collection_id = "2", collection = "def"),
            IndexedTrack(id = "10002", is_podcast = false, author_id = "123", collection_id = "3", collection = "def"),
        )
        indexedQueries.insertGroup(items)

        val actual = queries.selectArtistAlbums("123").executeAsList()

        val expected = listOf("1", "2", "3")
        Assert.assertEquals(expected, actual.map { it.id })
    }

}