package dev.olog.data.genre

import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.index.Indexed_genres
import dev.olog.data.index.Indexed_genres_playables
import dev.olog.data.insertGroup
import dev.olog.data.insertPlayableGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SortedGenreQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedPlayablesQueries = db.indexedPlayablesQueries
    private val indexedGenresQueries = db.indexedGenresQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.genresQueries

    @Before
    fun setup() {
        blacklistQueries.insert(directory = "dir")

        val genres = listOf(
            Indexed_genres(id = 1, name = "abc"),
            Indexed_genres(id = 3, name = "äaa"),
            Indexed_genres(id = 2, name = "zzz"),
            Indexed_genres(id = 4, name = "blacklisted"),
        )
        indexedGenresQueries.insertGroup(genres)

        val playables = listOf(
            AndroidIndexedPlayables(1, is_podcast = false),
            AndroidIndexedPlayables(2, is_podcast = false, author_id = 1, author = "author1"),
            AndroidIndexedPlayables(3, is_podcast = false, author_id = 2, author = "author2"),
            AndroidIndexedPlayables(4, is_podcast = false),
            AndroidIndexedPlayables(5, is_podcast = false),
            // below should be filtered
            AndroidIndexedPlayables(6, is_podcast = false, directory = "dir"),
            AndroidIndexedPlayables(7, is_podcast = true),
            AndroidIndexedPlayables(8, is_podcast = true),
            AndroidIndexedPlayables(9, is_podcast = true),
            AndroidIndexedPlayables(10, is_podcast = true),
        )
        indexedPlayablesQueries.insertGroup(playables)

        val genrePlayables = listOf(
            Indexed_genres_playables(1, 3),
            Indexed_genres_playables(1, 2),
            Indexed_genres_playables(2, 1),
            Indexed_genres_playables(3, 2),
            Indexed_genres_playables(3, 6),
            Indexed_genres_playables(3, 7),
        )
        indexedGenresQueries.insertPlayableGroup(genrePlayables)
    }

    @Test
    fun testSelectAllSortedByTitle() {
        // test ascending
        sortQueries.setGenresSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expected = listOf(
            Genres_view(3, "äaa", 1),
            Genres_view(1, "abc", 2),
            Genres_view(2, "zzz", 1),
        )
        Assert.assertEquals(expected, actualAsc)

        // test descending
        sortQueries.setGenresSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected.asReversed(), actualDesc)
    }

    @Test
    fun testSelectRelatedArtists() {
        val actual = queries.selectRelatedArtists(1).executeAsList()
        val expected = listOf(
            "author1",
            "author2",
        )
        Assert.assertEquals(expected, actual.map { it.author })
    }

    @Test
    fun testSelectSiblings() {
        val actual = queries.selectSiblings(1).executeAsList()
        val expected = listOf(
            Genres_view(3, "äaa", 1),
            Genres_view(2, "zzz", 1),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testSelectMostPlayed() {
        indexedPlayablesQueries.deleteAll()
        val playables = listOf(
            AndroidIndexedPlayables(1, is_podcast = false),
            AndroidIndexedPlayables(2, is_podcast = false),
            AndroidIndexedPlayables(3, is_podcast = false),
            AndroidIndexedPlayables(4, is_podcast = false),
            AndroidIndexedPlayables(5, is_podcast = false),
            AndroidIndexedPlayables(6, is_podcast = false),
            AndroidIndexedPlayables(7, is_podcast = false),
            AndroidIndexedPlayables(8, is_podcast = false),
            AndroidIndexedPlayables(9, is_podcast = false),
            AndroidIndexedPlayables(10, is_podcast = false),
            AndroidIndexedPlayables(11, is_podcast = false),
            AndroidIndexedPlayables(12, is_podcast = false),
        )
        indexedPlayablesQueries.insertGroup(playables)

        indexedGenresQueries.deleteAllPlayables()
        val genrePlayables = listOf(
            Indexed_genres_playables(1, 1),
            Indexed_genres_playables(1, 2),
            Indexed_genres_playables(1, 3),
            Indexed_genres_playables(1, 4),
            Indexed_genres_playables(1, 5),
            Indexed_genres_playables(1, 6),
            Indexed_genres_playables(1, 7),
            Indexed_genres_playables(1, 8),
            Indexed_genres_playables(1, 9),
            Indexed_genres_playables(1, 10),
            Indexed_genres_playables(1, 11),
        )
        indexedGenresQueries.insertPlayableGroup(genrePlayables)


        // insert most played
        repeat(10) { queries.incrementMostPlayed(100L, 2) }
        repeat(20) { queries.incrementMostPlayed(10L, 1) }
        repeat(8) { queries.incrementMostPlayed(8L, 1) }
        repeat(4) { queries.incrementMostPlayed(2L, 1) } // not present, at least 5

        // test skip less than 5 items
        Assert.assertEquals(
            listOf(
                10L to 20L,
                8L to 8L,
            ),
            queries.selectMostPlayed(1).executeAsList().map { it.id to it.counter }
        )

        repeat(10) { queries.incrementMostPlayed(9L, 1) }
        repeat(50) { queries.incrementMostPlayed(4L, 1) }
        repeat(19) { queries.incrementMostPlayed(3L, 1) }
        repeat(18) { queries.incrementMostPlayed(8L, 1) }
        repeat(15) { queries.incrementMostPlayed(1L, 1) }
        repeat(14) { queries.incrementMostPlayed(2L, 1) }
        repeat(13) { queries.incrementMostPlayed(7L, 1) }
        repeat(9) { queries.incrementMostPlayed(6L, 1) }
        repeat(5) { queries.incrementMostPlayed(5L, 1) }

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
            queries.selectMostPlayed(1).executeAsList().map { it.id to it.counter }
        )
    }

}