package dev.olog.data.playlist

import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.IndexedTrack
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.index.Indexed_playlists
import dev.olog.data.index.Indexed_playlists_playables
import dev.olog.data.insertGroup
import dev.olog.data.insertPlayableGroup
import dev.olog.data.sort.SortDao
import dev.olog.testing.IndexedPlaylistTracks
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SortedPlaylistQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedPlayablesQueries = db.indexedPlayablesQueries
    private val indexedPlaylistsQueries = db.indexedPlaylistsQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.playlistsQueries

    @Before
    fun setup() {
        blacklistQueries.insert(directory = "dir")

        val playlists = listOf(
            Indexed_playlists(id = "1", title = "abc", path = ""),
            Indexed_playlists(id = "3", title = "äaa", path = ""),
            Indexed_playlists(id = "2", title = "zzz", path = ""),
            Indexed_playlists(id = "4", title = "blacklisted", path = ""),
        )
        indexedPlaylistsQueries.insertGroup(playlists)

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

        val playlistPlayables = listOf(
            IndexedPlaylistTracks(id = "1", playlistId = "1", playableId = "3"),
            IndexedPlaylistTracks(id = "2", playlistId = "1", playableId = "2"),
            IndexedPlaylistTracks(id = "3", playlistId = "2", playableId = "1"),
            IndexedPlaylistTracks(id = "4", playlistId = "3", playableId = "2"),
            IndexedPlaylistTracks(id = "5", playlistId = "3", playableId = "6"),
            IndexedPlaylistTracks(id = "6", playlistId = "3", playableId = "7"),
        )
        indexedPlaylistsQueries.insertPlayableGroup(playlistPlayables)
    }

    @Test
    fun testSelectAllSortedByTitle() {
        // test ascending
        sortQueries.setPlaylistsSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expected = listOf(
            Playlists_view("3", "äaa", 1, ""),
            Playlists_view("1", "abc", 2, ""),
            Playlists_view("2", "zzz", 1, ""),
        )
        Assert.assertEquals(expected, actualAsc)

        // test descending
        sortQueries.setPlaylistsSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
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
            Playlists_view("3", "äaa", 1, ""),
            Playlists_view("2", "zzz", 1, ""),
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

        indexedPlaylistsQueries.deleteAllPlayables()
        val playlistPlayables = listOf(
            IndexedPlaylistTracks(id = "10", playlistId = "1", playableId = "1"),
            IndexedPlaylistTracks(id = "20", playlistId = "1", playableId = "2"),
            IndexedPlaylistTracks(id = "30", playlistId = "1", playableId = "3"),
            IndexedPlaylistTracks(id = "40", playlistId = "1", playableId = "4"),
            IndexedPlaylistTracks(id = "50", playlistId = "1", playableId = "5"),
            IndexedPlaylistTracks(id = "60", playlistId = "1", playableId = "6"),
            IndexedPlaylistTracks(id = "70", playlistId = "1", playableId = "7"),
            IndexedPlaylistTracks(id = "80", playlistId = "1", playableId = "8"),
            IndexedPlaylistTracks(id = "90", playlistId = "1", playableId = "9"),
            IndexedPlaylistTracks(id = "100", playlistId = "1", playableId = "10"),
            IndexedPlaylistTracks(id = "110", playlistId = "1", playableId = "11"),
        )
        indexedPlaylistsQueries.insertPlayableGroup(playlistPlayables)


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