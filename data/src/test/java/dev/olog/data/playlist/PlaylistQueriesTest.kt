package dev.olog.data.playlist

import dev.olog.data.*
import dev.olog.data.index.Indexed_playlists
import dev.olog.data.index.Indexed_playlists_playables
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class PlaylistQueriesTest {

    private val db = TestDatabase()
    private val indexedPlayableQueries = db.indexedPlayablesQueries
    private val indexedPlaylistQueries = db.indexedPlaylistsQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.playlistsQueries

    @Before
    fun setup() {
        blacklistQueries.insert("dir")

        indexedPlaylistQueries.insert(Indexed_playlists(1, "playlists1"))
        indexedPlaylistQueries.insert(Indexed_playlists(2, "playlists2"))
        indexedPlaylistQueries.insert(Indexed_playlists(3, "playlists no songs"))
        indexedPlaylistQueries.insert(Indexed_playlists(4L, "playlists with blacklisted blacklisted"))

        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 1, play_order = 7))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 2, play_order = 6))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 2, playable_id = 3, play_order = 5))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 2, playable_id = 4, play_order = 4))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 2, playable_id = 6, play_order = 3))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 3, playable_id = 5, play_order = 2))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 4, playable_id = 7, play_order = 1))

        indexedPlayableQueries.insert(IndexedPlayables(1L, is_podcast = false))
        indexedPlayableQueries.insert(IndexedPlayables(2L, is_podcast = false))
        indexedPlayableQueries.insert(IndexedPlayables(3L, is_podcast = false))
        indexedPlayableQueries.insert(IndexedPlayables(4L, is_podcast = false))
        // 5 missing
        indexedPlayableQueries.insert(IndexedPlayables(6, is_podcast = false))

        indexedPlayableQueries.insert(IndexedPlayables(7, is_podcast = false, directory = "dir"))
    }

    @Test
    fun `test selectAll`() {
        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            PlaylistView(id = 1, title = "playlists1", songs = 2),
            PlaylistView(id = 2, title = "playlists2", songs = 3),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById`() {
        val actual = queries.selectById(id = 1).executeAsOne()
        val expected = PlaylistView(id = 1, title = "playlists1", songs = 2)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById, should be null when not present`() {
        val actual = queries.selectById(id = 5).executeAsOneOrNull()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test selectRecentlyAddedSongs`() {
        fun song(songId: Long, dateAdded: Long, directory: String = "") = IndexedPlayables(
            id = songId,
            collection_id = 0,
            date_added = dateAdded,
            is_podcast = false,
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

        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 1, 1))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 2, 2))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 3, 3))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 2, playable_id = 4, 4))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 5, 5))
        indexedPlaylistQueries.insertPlayable(Indexed_playlists_playables(playlist_id = 1, playable_id = 6, 6))

        val actual = queries.selectRecentlyAddedSongs(1).executeAsList()
        val expected = listOf(1L, 3L, 2L)
        Assert.assertEquals(expected, actual.map { it.id })
    }

}