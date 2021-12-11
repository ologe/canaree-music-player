package dev.olog.data.collection

import dev.olog.core.entity.sort.CollectionSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
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
            AndroidIndexedPlayables(id = 1, collection_id = 1, collection = "abc", author = "abc2", is_podcast = false),
            AndroidIndexedPlayables(id = 2, collection_id = 1, collection = "abc", author = "abc2", is_podcast = false),
            AndroidIndexedPlayables(id = 3, collection_id = 2, collection = "äaa", author = "äaa2", is_podcast = false),
            AndroidIndexedPlayables(id = 4, collection_id = 3, collection = "<unknown>", author = "<unknown>", is_podcast = false),
            AndroidIndexedPlayables(id = 5, collection_id = 4, collection = "zzz", author = "zzz2", is_podcast = false),
            // filtered
            AndroidIndexedPlayables(id = 100L, collection_id = 1000, is_podcast = false, directory = "dir"),
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
            AndroidIndexedPlayables(id = 1, collection_id = 1, collection = "abc", author = "abc2", is_podcast = false),
            AndroidIndexedPlayables(id = 2, collection_id = 1, collection = "abc", author = "abc2", is_podcast = false),
            AndroidIndexedPlayables(id = 3, collection_id = 2, collection = "äaa", author = "äaa2", is_podcast = false),
            AndroidIndexedPlayables(id = 4, collection_id = 3, collection = "<unknown>", author = "<unknown>", is_podcast = false),
            AndroidIndexedPlayables(id = 5, collection_id = 4, collection = "zzz", author = "zzz2", is_podcast = false),
            // filtered
            AndroidIndexedPlayables(id = 100L, collection_id = 1000, is_podcast = false, directory = "dir"),
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
            AndroidIndexedPlayables(id = 10_000, is_podcast = false, author_id = 123, collection_id = 1, collection = "abc"),
            AndroidIndexedPlayables(id = 10_001, is_podcast = false, author_id = 123, collection_id = 2, collection = "def"),
            AndroidIndexedPlayables(id = 10_002, is_podcast = false, author_id = 123, collection_id = 3, collection = "def"),
        )
        indexedQueries.insertGroup(items)

        val actual = queries.selectArtistAlbums(123).executeAsList()

        val expected = listOf(1L, 2L, 3L)
        Assert.assertEquals(expected, actual.map { it.id })
    }

}