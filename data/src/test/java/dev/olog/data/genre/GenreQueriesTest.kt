package dev.olog.data.genre

import dev.olog.testing.GenreView
import dev.olog.testing.IndexedTrack
import dev.olog.data.QueriesConstants
import dev.olog.data.TestDatabase
import dev.olog.data.index.Indexed_genres
import dev.olog.data.index.Indexed_genres_playables
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class GenreQueriesTest {

    private val db = TestDatabase()
    private val indexedPlayableQueries = db.indexedPlayablesQueries
    private val indexedGenreQueries = db.indexedGenresQueries
    private val blacklistQueries = db.blacklistQueries
    private val queries = db.genresQueries

    @Before
    fun setup() {
        blacklistQueries.insert("dir")

        indexedGenreQueries.insert(Indexed_genres("1", "genre1"))
        indexedGenreQueries.insert(Indexed_genres("2", "genre2"))
        indexedGenreQueries.insert(Indexed_genres("3", "genre no songs"))
        indexedGenreQueries.insert(Indexed_genres("4", "genre with blacklisted blacklisted"))

        indexedGenreQueries.insertPlayable(Indexed_genres_playables("1", "1"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("1", "2"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("2", "3"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("2", "4"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("2", "6"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("3", "5"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables("4", "7"))

        indexedPlayableQueries.insert(IndexedTrack("1", is_podcast = false))
        indexedPlayableQueries.insert(IndexedTrack("2", is_podcast = false))
        indexedPlayableQueries.insert(IndexedTrack("3", is_podcast = false))
        indexedPlayableQueries.insert(IndexedTrack("4", is_podcast = false))
        // 5 missing
        indexedPlayableQueries.insert(IndexedTrack("6", is_podcast = false))

        indexedPlayableQueries.insert(IndexedTrack("7", is_podcast = false, directory = "dir"))
    }

    @Test
    fun `test selectAll`() {
        val actual = queries.selectAll().executeAsList()
        val expected = listOf(
            GenreView(id = "1", name = "genre1", songs = 2),
            GenreView(id = "2", name = "genre2", songs = 3),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectById`() {
        val actual = queries.selectById(id = "1").executeAsOne()
        val expected = GenreView(id = "1", name = "genre1", songs = 2)
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
            is_podcast = false,
            directory = directory,
        )

        indexedPlayableQueries.deleteAll()
        indexedGenreQueries.deleteAll()
        indexedGenreQueries.deleteAllPlayables()

        val unixTimestamp = QueriesConstants.unitTimestamp()
        indexedPlayableQueries.insert(song(songId = 1, dateAdded = unixTimestamp))
        indexedPlayableQueries.insert(song(songId = 2, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 1.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 3, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 3.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 4, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime + 2.days.inWholeSeconds))
        // below should be skipped
        indexedPlayableQueries.insert(song(songId = 5, dateAdded = unixTimestamp - QueriesConstants.recentlyAddedMaxTime - 1.days.inWholeSeconds))
        indexedPlayableQueries.insert(song(songId = 6, dateAdded = unixTimestamp, directory = "dir", ))

        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "1", song_id = "1"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "1", song_id = "2"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "1", song_id = "3"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "2", song_id = "4"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "1", song_id = "5"))
        indexedGenreQueries.insertPlayable(Indexed_genres_playables(genre_id = "1", song_id = "6"))

        val actual = queries.selectRecentlyAddedSongs("1").executeAsList()
        val expected = listOf("1", "3", "2")
        Assert.assertEquals(expected, actual.map { it.id })
    }

}