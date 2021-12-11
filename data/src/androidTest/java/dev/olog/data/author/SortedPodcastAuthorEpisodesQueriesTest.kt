package dev.olog.data.author

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.olog.core.entity.sort.*
import dev.olog.data.*
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SortedPodcastAuthorEpisodesQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.podcastAuthorsQueries

    @Before
    fun setup() {
        blacklistQueries.insert("yes")
        // item to be filtered, blacklisted and podcast
        indexedQueries.insert(AndroidIndexedPlayables(id = 1000, is_podcast = true, directory = "yes"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1001, is_podcast = false, directory = "no"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1002, is_podcast = false, directory = "yes"))

        // insert data
        indexedQueries.insertGroup(IndexedPodcastEpisodes)

        // should be filtered because of id
        val filtered = AndroidIndexedPlayables(
            id = 7,
            author_id = 1,
            title = "",
            album_artist = "",
            collection = "",
            is_podcast = true,
            duration = 0,
            date_added = 0
        )
        indexedQueries.insert(filtered)
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
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.title })

        // when descending
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.title })
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
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Collection, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
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
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Collection, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expectedDesc, actualDesc.map { it.collection to it.title })
    }

    @Test
    fun testObserveAllSortedByAlbumArtist() {
        // when ascending
        val expectedAsc = listOf(
            // ignore accents
            "äaa2" to "âspace",
            "azz2" to "àtitle",
            // second sort on title when same author
            "zee2" to "random",
            "zee2" to "zzz",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "ėspace",
            "<unknown>" to "êtitle",
        )
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.AlbumArtist, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expectedAsc, actualAsc.map { it.album_artist to it.title })

        // when descending
        val expectedDesc = listOf(
            // second sort on title when same author
            "zee2" to "zzz",
            "zee2" to "random",
            // ignore accents
            "azz2" to "àtitle",
            "äaa2" to "âspace",
            // <unknown> always last, second sort on title when same author
            "<unknown>" to "êtitle",
            "<unknown>" to "ėspace",
        )
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.AlbumArtist, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expectedDesc, actualDesc.map { it.album_artist to it.title })
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
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Duration, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.duration to it.title })

        // when descending
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.Duration, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.duration to it.title })
    }

    @Test
    fun testObserveAllSortedByDateAdded() {
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
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.DateAdded, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.date_added to it.title })

        // when descending
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.DateAdded, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.date_added to it.title })
    }

    @Test
    fun testObserveAllSortedByTrackNumber() {
        // when ascending
        val expected = listOf(
            Triple(0, 0, "zzz"),
            Triple(1, 1, "ėspace"),
            Triple(1, 2, "random"),
            Triple(2, 1, "âspace"),
            Triple(2, 2, "àtitle"),
            Triple(2, 3, "êtitle"),
        )

        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.TrackNumber, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { Triple(it.disc_number, it.track_number, it.title) })

        // when descending
        sortQueries.setDetailPodcastAuthorsSort(Sort(AuthorDetailSort.TrackNumber, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(author_id = 1000).executeAsList()
        Assert.assertEquals(expected.asReversed(), actualDesc.map { Triple(it.disc_number, it.track_number, it.title) })
    }

    // TODO test flow updates on blacklist and sort

}