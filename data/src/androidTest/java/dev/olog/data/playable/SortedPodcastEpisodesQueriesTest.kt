package dev.olog.data.playable

import dev.olog.core.entity.sort.PlayableSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.IndexedPodcastEpisodes
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SortedPodcastEpisodesQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.podcastEpisodesQueries

    @Before
    fun setup() {
        blacklistQueries.insert("yes")
        // item to be filtered, blacklisted and podcast
        indexedQueries.insert(AndroidIndexedPlayables(id = 1000, is_podcast = true, directory = "yes"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1001, is_podcast = false, directory = "no"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1002, is_podcast = false, directory = "yes"))

        indexedQueries.insertGroup(IndexedPodcastEpisodes)
    }

    @Test
    fun testObserveAllSortedByTitle() {
        // ignore accents
        val expected = listOf(
            "âspace",
            "àtitle",
            "ėspace",
            "êtitle",
            "random",
            "zzz",
        )

        // when ascending
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.title })

        // when descending
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.title })
    }

    @Test
    fun testObserveAllSortedByAuthor() {
        // when ascending
        val expectedAsc = listOf(
            // ignore accents
            "äaa" to "âspace",
            "azz" to "àtitle",
            // second sort on title when same author
            "zee" to "random",
            "zee" to "zzz",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "ėspace",
            "<unknown>" to "êtitle",
        )

        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Author, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expectedAsc, actualAsc.map { it.author to it.title })

        // when descending
        val expectedDesc = listOf(
            // second sort on title when same author
            "zee" to "zzz",
            "zee" to "random",
            // ignore accents
            "azz" to "àtitle",
            "äaa" to "âspace",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "êtitle",
            "<unknown>" to "ėspace",
        )

        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Author, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expectedDesc, actualDesc.map { it.author to it.title })
    }

    @Test
    fun testObserveAllSortedByCollection() {
        // when ascending
        val expectedAsc = listOf(
            // second sort on title when same collection
            "def" to "random",
            "def" to "zzz",
            // ignore accents
            "ïhello" to "âspace",
            "íttt" to "àtitle",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "ėspace",
            "<unknown>" to "êtitle",
        )
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Collection, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expectedAsc, actualAsc.map { it.collection to it.title })

        // when descending
        val expectedDesc = listOf(
            // ignore accents
            "íttt" to "àtitle",
            "ïhello" to "âspace",
            // second sort on title when same collection
            "def" to "zzz",
            "def" to "random",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "êtitle",
            "<unknown>" to "ėspace",
        )
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Collection, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expectedDesc, actualDesc.map { it.collection to it.title })
    }

    @Test
    fun testObserveAllSortedByDuration() {
        // when ascending
        val expected = listOf(
            15L to "âspace",
            20L to "àtitle",
            // second sort on title when same duration
            50L to "ėspace",
            50L to "êtitle",
            50L to "random",
            50L to "zzz",
        )
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Duration, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.duration to it.title })

        // when descending
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.Duration, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.duration to it.title })
    }

    @Test
    fun testObserveAllSortedByRecentlyAdded() {
        // when ascending
        val expected = listOf(
            // descending, second sort title ascending
            40L to "âspace",
            40L to "ėspace",
            40L to "random",
            25L to "zzz",
            15L to "àtitle",
            15L to "êtitle",
        )
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.DateAdded, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.date_added to it.title })

        // when descending
        sortQueries.setPodcastEpisodesSort(Sort(PlayableSort.DateAdded, SortDirection.DESCENDING))
        val actualDesc = queries.selectAllSorted().executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.date_added to it.title })
    }


    // TODO test flow updates on blacklist and sort

}