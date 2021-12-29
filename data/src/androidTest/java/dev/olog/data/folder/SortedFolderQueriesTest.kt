package dev.olog.data.folder

import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.IndexedTrack
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
            IndexedTrack(id = "1", directory = "abc", is_podcast = false),
            IndexedTrack(id = "2", directory = "abc", is_podcast = false),
            IndexedTrack(id = "3", directory = "äaa", is_podcast = false),
            IndexedTrack(id = "5", directory = "zzz", is_podcast = false),
            // filtered
            IndexedTrack(id = "100", directory = "dir", is_podcast = false),
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
            IndexedTrack(id = "100", is_podcast = false, directory = "relatedArtists", author_id = "1", author = "author1"),
            IndexedTrack(id = "101", is_podcast = false, directory = "relatedArtists", author_id = "2", author = "author2"),
            IndexedTrack(id = "102", is_podcast = false, directory = "relatedArtists", author_id = "3", author = "author3"),
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
            IndexedTrack(id = "1", directory = "abc", is_podcast = false),
            IndexedTrack(id = "2", directory = "def", is_podcast = false),
            IndexedTrack(id = "3", directory = "def", is_podcast = false),
            IndexedTrack(id = "5", directory = "ghi", is_podcast = false),
            // filtered
            IndexedTrack(id = "100", directory = "dir", is_podcast = false),
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
            IndexedTrack(id = "1", directory = "abc", is_podcast = false),
            IndexedTrack(id = "2", directory = "def", is_podcast = false),
            IndexedTrack(id = "3", directory = "def", is_podcast = false),
            IndexedTrack(id = "5", directory = "ghi", is_podcast = false),
            // not filtered here
            IndexedTrack(id = "100", directory = "dir", is_podcast = false),
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
            IndexedTrack("1", is_podcast = false, directory = "abc"),
            IndexedTrack("2", is_podcast = false, directory = "abc"),
            IndexedTrack("3", is_podcast = false, directory = "abc"),
            IndexedTrack("4", is_podcast = false, directory = "abc"),
            IndexedTrack("5", is_podcast = false, directory = "abc"),
            IndexedTrack("6", is_podcast = false, directory = "abc"),
            IndexedTrack("7", is_podcast = false, directory = "abc"),
            IndexedTrack("8", is_podcast = false, directory = "abc"),
            IndexedTrack("9", is_podcast = false, directory = "abc"),
            IndexedTrack("10", is_podcast = false, directory = "abc"),
            // below will be filtered
            IndexedTrack(id = "100", is_podcast = false, directory = "random dir"),
            IndexedTrack(id = "101", is_podcast = false, directory = "dir"),
            IndexedTrack(id = "102", is_podcast = true),
        )
        indexedQueries.insertGroup(items)

        // insert most played
        repeat(10) { queries.incrementMostPlayed("100", dir = "random dir") }
        repeat(20) { queries.incrementMostPlayed("10", dir = "abc") }
        repeat(8) { queries.incrementMostPlayed("8", dir = "abc") }
        repeat(4) { queries.incrementMostPlayed("2", dir = "abc") } // not present, at least 5

        // test skip less than 5 items
        Assert.assertEquals(
            listOf(
                "10" to 20L,
                "8" to 8L,
            ),
            queries.selectMostPlayed("abc").executeAsList().map { it.id to it.counter }
        )

        repeat(10) { queries.incrementMostPlayed("9", dir = "abc") }
        repeat(50) { queries.incrementMostPlayed("4", dir = "abc") }
        repeat(19) { queries.incrementMostPlayed("3", dir = "abc") }
        repeat(18) { queries.incrementMostPlayed("8", dir = "abc") }
        repeat(15) { queries.incrementMostPlayed("1", dir = "abc") }
        repeat(14) { queries.incrementMostPlayed("2", dir = "abc") }
        repeat(13) { queries.incrementMostPlayed("7", dir = "abc") }
        repeat(9) { queries.incrementMostPlayed("6", dir = "abc") }
        repeat(5) { queries.incrementMostPlayed("5", dir = "abc") }

        Assert.assertEquals(
            listOf(
                "4" to 50L,
                "8" to 26L,
                "10" to 20L,
                "3" to 19L,
                "2" to 18L,
                "1" to 15L,
                "7" to 13L,
                "9" to 10L,
                "6" to 9L,
                "5" to 5L,
            ),
            queries.selectMostPlayed("abc").executeAsList().map { it.id to it.counter }
        )
    }

}