package dev.olog.data.author

import dev.olog.core.entity.sort.AuthorSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Test

class SortedArtistQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.artistsQueries

    @Test
    fun testSelectAllSortedByName() {
        blacklistQueries.insert(directory = "dir")

        val items = listOf(
            AndroidIndexedPlayables(id = 1, author_id = 1, author = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 2, author_id = 1, author = "abc", is_podcast = false),
            AndroidIndexedPlayables(id = 3, author_id = 2, author = "äaa", is_podcast = false),
            AndroidIndexedPlayables(id = 4, author_id = 3, author = "<unknown>", is_podcast = false),
            AndroidIndexedPlayables(id = 5, author_id = 4, author = "zzz", is_podcast = false),
            // filtered
            AndroidIndexedPlayables(id = 100L, author_id = 1000, is_podcast = false, directory = "dir"),
        )

        indexedQueries.insertGroup(items)

        // test ascending
        sortQueries.setArtistsSort(Sort(AuthorSort.Name, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expectedAsc = listOf(
            "äaa",
            "abc",
            "zzz",
            "<unknown>"
        )
        Assert.assertEquals(expectedAsc, actualAsc.map { it.name })

        // test descending
        sortQueries.setArtistsSort(Sort(AuthorSort.Name, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        val expectedDesc = listOf(
            "zzz",
            "abc",
            "äaa",
            "<unknown>"
        )
        Assert.assertEquals(expectedDesc, actualDesc.map { it.name })
    }

}