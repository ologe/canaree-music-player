package dev.olog.data.mediastore.song.genre

import dev.olog.data.DatabaseTest
import dev.olog.data.emptyMediaStoreGenreEntity
import dev.olog.data.emptyMediaStoreGenreTrackEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreGenreDaoTest : DatabaseTest() {

    private val dao = db.mediaStoreGenreDao()

    @Test
    fun testGenres() = runTest {
        // initial state, empty
        Assert.assertEquals(emptyList<List<MediaStoreGenreEntity>>(), dao.getAllGenres())

        // insert one item
        dao.insertAllGenres(listOf(emptyMediaStoreGenreEntity(id = "1")))

        Assert.assertEquals(listOf(emptyMediaStoreGenreEntity(id = "1")), dao.getAllGenres())

        // insert multiple items, also with conflicts
        dao.insertAllGenres(listOf(emptyMediaStoreGenreEntity(id = "1")))
        dao.insertAllGenres(listOf(
            emptyMediaStoreGenreEntity(id = "1"),
            emptyMediaStoreGenreEntity(id = "2"),
            emptyMediaStoreGenreEntity(id = "3"),
        ))

        Assert.assertEquals(
            listOf(
                emptyMediaStoreGenreEntity(id = "1"),
                emptyMediaStoreGenreEntity(id = "2"),
                emptyMediaStoreGenreEntity(id = "3"),
            ),
            dao.getAllGenres()
        )

        // clear
        dao.deleteAllGenres()

        Assert.assertEquals(emptyList<List<MediaStoreGenreEntity>>(), dao.getAllGenres())
    }

    @Test
    fun testGenreTracks() = runTest {
        // initial state, empty
        Assert.assertEquals(emptyList<List<MediaStoreGenreTrackEntity>>(), dao.getAllGenreTracks())

        // insert one item
        dao.insertAllGenreTracks(listOf(emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10")))

        Assert.assertEquals(listOf(emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10")), dao.getAllGenreTracks())

        // insert multiple items, also with conflicts
        dao.insertAllGenreTracks(listOf(emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10")))
        dao.insertAllGenreTracks(listOf(
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "20"),
        ))

        Assert.assertEquals(
            listOf(
                emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
                emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "10"),
                emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
                emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "20"),
            ),
            dao.getAllGenreTracks()
        )

        // clear
        dao.deleteAllGenreTracks()

        Assert.assertEquals(emptyList<List<MediaStoreGenreTrackEntity>>(), dao.getAllGenreTracks())
    }

}