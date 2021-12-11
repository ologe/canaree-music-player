package dev.olog.data.folder

import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Test

class SortedFolderQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.foldersQueries

    @Test
    fun testSelectAllSortedByTitle() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            AndroidIndexedPlayables(id = 1, directory = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 2, directory = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 3, directory = "äaa", is_podcast = false),
            AndroidIndexedPlayables(id = 5, directory = "zzz", is_podcast = false),
            // filtered
            AndroidIndexedPlayables(id = 100L, directory = "dir", is_podcast = false),
        )

        indexedQueries.insertGroup(items)

        // test ascending
        sortQueries.setFoldersSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expectedAsc = listOf(
            "äaa",
            "abc",
            "zzz",
        )
        Assert.assertEquals(expectedAsc, actualAsc.map { it.directory })

        // test descending
        sortQueries.setFoldersSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        val expectedDesc = listOf(
            "zzz",
            "abc",
            "äaa",
        )
        Assert.assertEquals(expectedDesc, actualDesc.map { it.directory })
    }

    @Test
    fun testSelectRelatedArtists() {
        val items = listOf(
            AndroidIndexedPlayables(id = 100, is_podcast = false, directory = "relatedArtists", author_id = 1, author = "author1"),
            AndroidIndexedPlayables(id = 101, is_podcast = false, directory = "relatedArtists", author_id = 2, author = "author2"),
            AndroidIndexedPlayables(id = 102, is_podcast = false, directory = "relatedArtists", author_id = 3, author = "author3"),
        )
        indexedQueries.insertGroup(items)

        val actual = queries.selectRelatedArtists("relatedArtists").executeAsList()
        val expected = listOf(
            "author1",
            "author2",
            "author3",
        )
        Assert.assertEquals(expected, actual.map { it.name })
    }

    @Test
    fun testSelectSiblings() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            AndroidIndexedPlayables(id = 1, directory = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 2, directory = "def", is_podcast = false),
            AndroidIndexedPlayables(id = 3, directory = "def", is_podcast = false),
            AndroidIndexedPlayables(id = 5, directory = "ghi", is_podcast = false),
            // filtered
            AndroidIndexedPlayables(id = 100L, directory = "dir", is_podcast = false),
        )

        indexedQueries.insertGroup(items)

        val actual = queries.selectSiblings("abc").executeAsList()
        val expected = listOf(
            "def" to 2L,
            "ghi" to 1L,
        )
        Assert.assertEquals(expected, actual.map { it.directory to it.songs })
    }

    @Test
    fun testSelectAllBlacklistedIncluded() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            AndroidIndexedPlayables(id = 1, directory = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 2, directory = "def", is_podcast = false),
            AndroidIndexedPlayables(id = 3, directory = "def", is_podcast = false),
            AndroidIndexedPlayables(id = 5, directory = "ghi", is_podcast = false),
            // not filtered here
            AndroidIndexedPlayables(id = 100L, directory = "dir", is_podcast = false),
        )

        indexedQueries.insertGroup(items)

        val actual = queries.selectAllBlacklistedIncluded().executeAsList()
        val expected = listOf(
            "abc" to 1L,
            "def" to 2L,
            "dir" to 1L,
            "ghi" to 1L,
        )
        Assert.assertEquals(expected, actual.map { it.directory to it.songs })
    }

    @Test
    fun testSelectMostPlayed() {
        blacklistQueries.insert(directory = "dir")

        // insert songs
        val items = listOf(
            AndroidIndexedPlayables(1L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(2L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(3L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(4L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(5L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(6L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(7L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(8L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(9L, is_podcast = false, directory = "abc"),
            AndroidIndexedPlayables(10L, is_podcast = false, directory = "abc"),
            // below will be filtered
            AndroidIndexedPlayables(id = 100, is_podcast = false, directory = "random dir"),
            AndroidIndexedPlayables(id = 101, is_podcast = false, directory = "dir"),
            AndroidIndexedPlayables(id = 102, is_podcast = true),
        )
        indexedQueries.insertGroup(items)

        // insert most played
        repeat(10) { queries.incrementMostPlayed(100L, dir = "random dir") }
        repeat(20) { queries.incrementMostPlayed(10L, dir = "abc") }
        repeat(8) { queries.incrementMostPlayed(8L, dir = "abc") }
        repeat(4) { queries.incrementMostPlayed(2L, dir = "abc") } // not present, at least 5

        // test skip less than 5 items
        Assert.assertEquals(
            listOf(
                10L to 20L,
                8L to 8L,
            ),
            queries.selectMostPlayed("abc").executeAsList().map { it.id to it.counter }
        )

        repeat(10) { queries.incrementMostPlayed(9L, dir = "abc") }
        repeat(50) { queries.incrementMostPlayed(4L, dir = "abc") }
        repeat(19) { queries.incrementMostPlayed(3L, dir = "abc") }
        repeat(18) { queries.incrementMostPlayed(8L, dir = "abc") }
        repeat(15) { queries.incrementMostPlayed(1L, dir = "abc") }
        repeat(14) { queries.incrementMostPlayed(2L, dir = "abc") }
        repeat(13) { queries.incrementMostPlayed(7L, dir = "abc") }
        repeat(9) { queries.incrementMostPlayed(6L, dir = "abc") }
        repeat(5) { queries.incrementMostPlayed(5L, dir = "abc") }

        Assert.assertEquals(
            listOf(
                4L to 50L,
                8L to 26L,
                10L to 20L,
                3L to 19L,
                2L to 18L,
                1L to 15L,
                7L to 13L,
                9L to 10L,
                6L to 9L,
                5L to 5L,
            ),
            queries.selectMostPlayed("abc").executeAsList().map { it.id to it.counter }
        )
    }

}