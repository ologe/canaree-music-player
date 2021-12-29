package dev.olog.data.playlist

import dev.olog.data.*
import dev.olog.data.index.Indexed_playlists
import dev.olog.data.index.Indexed_playlists_playables
import dev.olog.testing.IndexedPlaylistTracks
import dev.olog.testing.IndexedTrack
import dev.olog.testing.PodcastPlaylistView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class PodcastPlaylistQueriesTest {

    private val db = TestDatabase()
    private val indexedPlayableQueries = db.indexedPlayablesQueries
    private val indexedPlaylistQueries = db.indexedPlaylistsQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.podcastPlaylistsQueries

    @Before
    fun setup() {
        blacklistQueries.insert("dir")

        indexedPlaylistQueries.insert(Indexed_playlists("1", "playlists1", ""))
        indexedPlaylistQueries.insert(Indexed_playlists("2", "playlists2", ""))
        indexedPlaylistQueries.insert(Indexed_playlists("3", "playlists no songs", ""))
        indexedPlaylistQueries.insert(Indexed_playlists("4", "playlists with blacklisted blacklisted", ""))

        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "10", playlistId = "1", playableId = "1", playOrder = 7))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "20", playlistId = "1", playableId = "2", playOrder = 6))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "30", playlistId = "2", playableId = "3", playOrder = 5))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "40", playlistId = "2", playableId = "4", playOrder = 4))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "60", playlistId = "2", playableId = "6", playOrder = 3))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "50", playlistId = "3", playableId = "5", playOrder = 2))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "70", playlistId = "4", playableId = "7", playOrder = 1))

        indexedPlayableQueries.insert(IndexedTrack("1", is_podcast = true))
        indexedPlayableQueries.insert(IndexedTrack("2", is_podcast = true))
        indexedPlayableQueries.insert(IndexedTrack("3", is_podcast = true))
        indexedPlayableQueries.insert(IndexedTrack("4", is_podcast = true))
        // 5 missing
        indexedPlayableQueries.insert(IndexedTrack("6", is_podcast = true))

        indexedPlayableQueries.insert(IndexedTrack("7", is_podcast = true, directory = "dir"))
    }

    @Test
    fun `test selectAll`() {
        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            PodcastPlaylistView(id = "1", title = "playlists1", songs = 2),
            PodcastPlaylistView(id = "2", title = "playlists2", songs = 3),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById`() {
        val actual = queries.selectById(id = "1").executeAsOne()
        val expected = PodcastPlaylistView(id = "1", title = "playlists1", songs = 2)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById, should be null when not present`() {
        val actual = queries.selectById(id = "5").executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test selectRecentlyAddedSongs`() {
        fun song(songId: Long, dateAdded: Long, directory: String = "") = IndexedTrack(
            id = songId.toString(),
            collection_id = "",
            date_added = dateAdded,
            is_podcast = true,
            directory = directory,
        )

        indexedPlayableQueries.deleteAll()
        indexedPlaylistQueries.deleteAll()
        indexedPlaylistQueries.deleteAllPlayables()

        val unixTimestamp = QueriesConstants.unitTimestamp()
        indexedPlayableQueries.insert(song(songId = 1, dateAdded = unixTimestamp))
        indexedPlayableQueries.insert(song(songId = 2, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 1.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 3, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 3.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 4, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 2.days.inWholeSeconds))
        // below should be skipped
        indexedPlayableQueries.insert(song(songId = 5, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime - 1.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 6, dateAdded = unixTimestamp, directory = "dir", ))

        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "10", playlistId = "1", playableId = "1", playOrder = 1))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "20", playlistId = "1", playableId = "2", playOrder = 2))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "30", playlistId = "1", playableId = "3", playOrder = 3))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "40", playlistId = "2", playableId = "4", playOrder = 4))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "50", playlistId = "1", playableId = "5", playOrder = 5))
        indexedPlaylistQueries.insertPlayable(IndexedPlaylistTracks(id = "60", playlistId = "1", playableId = "6", playOrder = 6))

        val actual = queries.selectRecentlyAddedSongs("1").executeAsList()
        val expected = listOf("1", "3", "2")
        Assert.assertEquals(expected, actual.map { it.id })
    }

}