package dev.olog.data.playlist

import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.AndroidIndexedPlayables
import dev.olog.data.AndroidTestDatabase
import dev.olog.data.index.Indexed_playlists
import dev.olog.data.index.Indexed_playlists_playables
import dev.olog.data.insertGroup
import dev.olog.data.insertPlayableGroup
import dev.olog.data.sort.SortDao
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
            Indexed_playlists(id = 1, title = "abc", path = ""),
            Indexed_playlists(id = 3, title = "äaa", path = ""),
            Indexed_playlists(id = 2, title = "zzz", path = ""),
            Indexed_playlists(id = 4, title = "blacklisted", path = ""),
        )
        indexedPlaylistsQueries.insertGroup(playlists)

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

        val playlistPlayables = listOf(
            Indexed_playlists_playables(id = 1, playlist_id = 1, playable_id = 3, play_order = 0),
            Indexed_playlists_playables(id = 2, playlist_id = 1, playable_id = 2, play_order = 0),
            Indexed_playlists_playables(id = 3, playlist_id = 2, playable_id = 1, play_order = 0),
            Indexed_playlists_playables(id = 4, playlist_id = 3, playable_id = 2, play_order = 0),
            Indexed_playlists_playables(id = 5, playlist_id = 3, playable_id = 6, play_order = 0),
            Indexed_playlists_playables(id = 6, playlist_id = 3, playable_id = 7, play_order = 0),
        )
        indexedPlaylistsQueries.insertPlayableGroup(playlistPlayables)
    }

    @Test
    fun testSelectAllSortedByTitle() {
        // test ascending
        sortQueries.setPlaylistsSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expected = listOf(
            Playlists_view(3, "äaa", 1, ""),
            Playlists_view(1, "abc", 2, ""),
            Playlists_view(2, "zzz", 1, ""),
        )
        Assert.assertEquals(expected, actualAsc)

        // test descending
        sortQueries.setPlaylistsSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
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
            Playlists_view(3, "äaa", 1, ""),
            Playlists_view(2, "zzz", 1, ""),
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

        indexedPlaylistsQueries.deleteAllPlayables()
        val playlistPlayables = listOf(
            Indexed_playlists_playables(id = 10, playlist_id = 1, playable_id = 1, play_order = 0),
            Indexed_playlists_playables(id = 20, playlist_id = 1, playable_id = 2, play_order = 0),
            Indexed_playlists_playables(id = 30, playlist_id = 1, playable_id = 3, play_order = 0),
            Indexed_playlists_playables(id = 40, playlist_id = 1, playable_id = 4, play_order = 0),
            Indexed_playlists_playables(id = 50, playlist_id = 1, playable_id = 5, play_order = 0),
            Indexed_playlists_playables(id = 60, playlist_id = 1, playable_id = 6, play_order = 0),
            Indexed_playlists_playables(id = 70, playlist_id = 1, playable_id = 7, play_order = 0),
            Indexed_playlists_playables(id = 80, playlist_id = 1, playable_id = 8, play_order = 0),
            Indexed_playlists_playables(id = 90, playlist_id = 1, playable_id = 9, play_order = 0),
            Indexed_playlists_playables(id = 100, playlist_id = 1, playable_id = 10, play_order = 0),
            Indexed_playlists_playables(id = 110, playlist_id = 1, playable_id = 11, play_order = 0),
        )
        indexedPlaylistsQueries.insertPlayableGroup(playlistPlayables)


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