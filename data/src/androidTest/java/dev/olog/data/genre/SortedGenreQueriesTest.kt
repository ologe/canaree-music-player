package dev.olog.data.genre

import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.IndexedTrack
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
            Indexed_genres(id = "1", name = "abc"),
            Indexed_genres(id = "3", name = "äaa"),
            Indexed_genres(id = "2", name = "zzz"),
            Indexed_genres(id = "4", name = "blacklisted"),
        )
        indexedGenresQueries.insertGroup(genres)

        val playables = listOf(
            IndexedTrack("1", is_podcast = false),
            IndexedTrack("2", is_podcast = false, author_id = "1", author = "author1"),
            IndexedTrack("3", is_podcast = false, author_id = "2", author = "author2"),
            IndexedTrack("4", is_podcast = false),
            IndexedTrack("5", is_podcast = false),
            // below should be filtered
            IndexedTrack("6", is_podcast = false, directory = "dir"),
            IndexedTrack("7", is_podcast = true),
            IndexedTrack("8", is_podcast = true),
            IndexedTrack("9", is_podcast = true),
            IndexedTrack("10", is_podcast = true),
        )
        indexedPlayablesQueries.insertGroup(playables)

        val genrePlayables = listOf(
            Indexed_genres_playables("1", "3"),
            Indexed_genres_playables("1", "2"),
            Indexed_genres_playables("2", "1"),
            Indexed_genres_playables("3", "2"),
            Indexed_genres_playables("3", "6"),
            Indexed_genres_playables("3", "7"),
        )
        indexedGenresQueries.insertPlayableGroup(genrePlayables)
    }

    @Test
    fun testSelectAllSortedByTitle() {
        // test ascending
        sortQueries.setGenresSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expected = listOf(
            Genres_view("3", "äaa", 1),
            Genres_view("1", "abc", 2),
            Genres_view("2", "zzz", 1),
        )
        Assert.assertEquals(expected, actualAsc)

        // test descending
        sortQueries.setGenresSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected.asReversed(), actualDesc)
    }

    @Test
    fun testSelectRelatedArtists() {
        val actual = queries.selectRelatedArtists("1").executeAsList()
        val expected = listOf(
            "author1",
            "author2",
        )
        Assert.assertEquals(expected, actual.map { it.author })
    }

    @Test
    fun testSelectSiblings() {
        val actual = queries.selectSiblings("1").executeAsList()
        val expected = listOf(
            Genres_view("3", "äaa", 1),
            Genres_view("2", "zzz", 1),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testSelectMostPlayed() {
        indexedPlayablesQueries.deleteAll()
        val playables = listOf(
            IndexedTrack("1", is_podcast = false),
            IndexedTrack("2", is_podcast = false),
            IndexedTrack("3", is_podcast = false),
            IndexedTrack("4", is_podcast = false),
            IndexedTrack("5", is_podcast = false),
            IndexedTrack("6", is_podcast = false),
            IndexedTrack("7", is_podcast = false),
            IndexedTrack("8", is_podcast = false),
            IndexedTrack("9", is_podcast = false),
            IndexedTrack("10", is_podcast = false),
            IndexedTrack("11", is_podcast = false),
            IndexedTrack("12", is_podcast = false),
        )
        indexedPlayablesQueries.insertGroup(playables)

        indexedGenresQueries.deleteAllPlayables()
        val genrePlayables = listOf(
            Indexed_genres_playables("1", "1"),
            Indexed_genres_playables("1", "2"),
            Indexed_genres_playables("1", "3"),
            Indexed_genres_playables("1", "4"),
            Indexed_genres_playables("1", "5"),
            Indexed_genres_playables("1", "6"),
            Indexed_genres_playables("1", "7"),
            Indexed_genres_playables("1", "8"),
            Indexed_genres_playables("1", "9"),
            Indexed_genres_playables("1", "10"),
            Indexed_genres_playables("1", "11"),
        )
        indexedGenresQueries.insertPlayableGroup(genrePlayables)


        // insert most played
        repeat(10) { queries.incrementMostPlayed("100", "2") }
        repeat(20) { queries.incrementMostPlayed("10", "1") }
        repeat(8) { queries.incrementMostPlayed("8", "1") }
        repeat(4) { queries.incrementMostPlayed("2", "1") } // not present, at least 5

        // test skip less than 5 items
        Assert.assertEquals(
            listOf(
                "10" to 20L,
                "8" to 8L,
            ),
            queries.selectMostPlayed("1").executeAsList().map { it.id to it.counter }
        )

        repeat(10) { queries.incrementMostPlayed("9", "1") }
        repeat(50) { queries.incrementMostPlayed("4", "1") }
        repeat(19) { queries.incrementMostPlayed("3", "1") }
        repeat(18) { queries.incrementMostPlayed("8", "1") }
        repeat(15) { queries.incrementMostPlayed("1", "1") }
        repeat(14) { queries.incrementMostPlayed("2", "1") }
        repeat(13) { queries.incrementMostPlayed("7", "1") }
        repeat(9) { queries.incrementMostPlayed("6", "1") }
        repeat(5) { queries.incrementMostPlayed("5", "1") }

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
            queries.selectMostPlayed("1").executeAsList().map { it.id to it.counter }
        )
    }

}