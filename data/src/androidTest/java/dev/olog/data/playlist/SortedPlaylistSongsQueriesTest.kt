package dev.olog.data.playlist

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.olog.core.entity.sort.*
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.IndexedSongs
import dev.olog.data.index.Indexed_playlists_playables
import dev.olog.data.insertGroup
import dev.olog.data.sort.SortDao
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SortedPlaylistSongsQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedPlayablesQueries = db.indexedPlayablesQueries
    private val indexedPlaylistsQueries = db.indexedPlaylistsQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.playlistsQueries

    @Before
    fun setup() {
        blacklistQueries.insert("yes")
        // item to be filtered, blacklisted and podcast
        indexedPlayablesQueries.insert(AndroidIndexedPlayables(id = 1000, is_podcast = false, directory = "yes"))
        indexedPlayablesQueries.insert(AndroidIndexedPlayables(id = 1001, is_podcast = true, directory = "no"))
        indexedPlayablesQueries.insert(AndroidIndexedPlayables(id = 1002, is_podcast = true, directory = "yes"))

        indexedPlayablesQueries.insertGroup(IndexedSongs)

        for ((index, song) in IndexedSongs.reversed().withIndex()) {
            indexedPlaylistsQueries.insertPlayable(Indexed_playlists_playables(1, song.id, play_order = index.toLong()))
        }
        indexedPlaylistsQueries.insertPlayable(Indexed_playlists_playables(1, 1000, play_order = 0))
        indexedPlaylistsQueries.insertPlayable(Indexed_playlists_playables(1, 1001, play_order = 0))
        indexedPlaylistsQueries.insertPlayable(Indexed_playlists_playables(1, 1002, play_order = 0))
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.title })

        // when descending
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Title, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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

        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Author, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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

        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Author, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Collection, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Collection, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.AlbumArtist, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.AlbumArtist, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Duration, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.duration to it.title })

        // when descending
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Duration, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
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
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.DateAdded, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.date_added to it.title })

        // when descending
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.DateAdded, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.date_added to it.title })
    }

    @Test
    fun testObserveAllSortedByCustom() {
        // when ascending
        val expected = listOf(
            // descending, second sort title ascending
            0L to 6L,
            1L to 5L,
            2L to 4L,
            3L to 3L,
            4L to 2L,
            5L to 1L,
        )
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Custom, SortDirection.ASCENDING))
        val actualAsc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected, actualAsc.map { it.play_order to it.id })

        // when descending
        sortQueries.setDetailPlaylistsSort(Sort(PlaylistDetailSort.Custom, SortDirection.DESCENDING))
        val actualDesc = queries.selectTracksByIdSorted(playlist_id = 1).executeAsList()
        Assert.assertEquals(expected.reversed(), actualDesc.map { it.play_order to it.id })
    }

    // TODO test flow updates on blacklist and sort

}