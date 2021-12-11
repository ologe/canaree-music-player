package dev.olog.data.folder

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.olog.core.entity.sort.*
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.IndexedSongs
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SortedFolderSongsQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedQueries = db.indexedPlayablesQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.foldersQueries

    @Before
    fun setup() {
        blacklistQueries.insert("yes")
        // item to be filtered, blacklisted and podcast
        indexedQueries.insert(AndroidIndexedPlayables(id = 1000, is_podcast = false, directory = "yes"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1001, is_podcast = true, directory = "no"))
        indexedQueries.insert(AndroidIndexedPlayables(id = 1002, is_podcast = true, directory = "yes"))

        indexedQueries.insertGroup(IndexedSongs.map { it.copy(directory = "directory") })
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.title })

        // when descending
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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

        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Author, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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

        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Author, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Collection, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Collection, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.AlbumArtist, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.AlbumArtist, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Duration, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.duration to it.title })

        // when descending
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.Duration, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.DateAdded, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.date_added to it.title })

        // when descending
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.DateAdded, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
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

        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.TrackNumber, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
        Assert.assertEquals(expected, actualAsc.map { Triple(it.disc_number, it.track_number, it.title) })

        // when descending
        sortQueries.setDetailFoldersSort(Sort(FolderDetailSort.TrackNumber, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(directory = "directory").executeAsList()
        Assert.assertEquals(expected.asReversed(), actualDesc.map { Triple(it.disc_number, it.track_number, it.title) })
    }


    // TODO test flow updates on blacklist and sort

}