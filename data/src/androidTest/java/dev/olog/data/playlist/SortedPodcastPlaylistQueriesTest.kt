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

class SortedPodcastPlaylistQueriesTest {

    private val db = AndroidTestDatabase()
    private val indexedPlayablesQueries = db.indexedPlayablesQueries
    private val indexedPlaylistsQueries = db.indexedPlaylistsQueries
    private val blacklistQueries = db.blacklistQueries
    private val sortQueries = SortDao(db.sortQueries)
    private val queries = db.podcastPlaylistsQueries

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
            IndexedTrack("1", is_podcast = true),
            IndexedTrack("2", is_podcast = true, author_id = "1", author = "author1"),
            IndexedTrack("3", is_podcast = true, author_id = "2", author = "author2"),
            IndexedTrack("4", is_podcast = true),
            IndexedTrack("5", is_podcast = true),
            // below should be filtered
            IndexedTrack("6", is_podcast = true, directory = "dir"),
            IndexedTrack("7", is_podcast = false),
            IndexedTrack("8", is_podcast = false),
            IndexedTrack("9", is_podcast = false),
            IndexedTrack("10", is_podcast = false),
        )
        indexedPlayablesQueries.insertGroup(playables)

        val playlistPlayables = listOf(
            IndexedPlaylistTracks(playlistId = "1", playableId = "3"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "2"),
            IndexedPlaylistTracks(playlistId = "2", playableId = "1"),
            IndexedPlaylistTracks(playlistId = "3", playableId = "2"),
            IndexedPlaylistTracks(playlistId = "3", playableId = "6"),
            IndexedPlaylistTracks(playlistId = "3", playableId = "7"),
        )
        indexedPlaylistsQueries.insertPlayableGroup(playlistPlayables)
    }

    @Test
    fun testSelectAllSortedByTitle() {
        // test ascending
        sortQueries.setPodcastPlaylistsSort(Sort(GenericSort.Title, SortDirection.ASCENDING))
        val actualAsc = queries.selectAllSorted().executeAsList()
        val expected = listOf(
            Podcast_playlists_view(id = "3", title = "äaa", songs = 1, path = ""),
            Podcast_playlists_view(id = "1", title = "abc", songs = 2, path = ""),
            Podcast_playlists_view(id = "2", title = "zzz", songs = 1, path = ""),
        )
        Assert.assertEquals(expected, actualAsc)

        // test descending
        sortQueries.setPodcastPlaylistsSort(Sort(GenericSort.Title, SortDirection.DESCENDING))
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
            Podcast_playlists_view(id = "3", title = "äaa", songs = 1, path = ""),
            Podcast_playlists_view(id = "2", title = "zzz", songs = 1, path = ""),
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
            IndexedPlaylistTracks(playlistId = "1", playableId = "1"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "2"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "3"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "4"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "5"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "6"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "7"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "8"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "9"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "10"),
            IndexedPlaylistTracks(playlistId = "1", playableId = "11"),
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
                "10" to 20L,
                "8" to 8L,
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
            queries.selectMostPlayed(1).executeAsList().map { it.id to it.counter }
        )
    }

}