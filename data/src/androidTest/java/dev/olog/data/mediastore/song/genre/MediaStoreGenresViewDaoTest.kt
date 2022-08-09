package dev.olog.data.mediastore.song.genre

import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.GenreSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DatabaseTest
import dev.olog.data.blacklist.db.BlacklistEntity
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreGenreEntity
import dev.olog.data.emptyMediaStoreGenreTrackEntity
import dev.olog.data.emptyMediaStoreGenresView
import dev.olog.data.emptyMediaStoreGenresViewSorted
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreGenresViewDaoTest : DatabaseTest() {

    private val mediaStoreAudioDao = db.mediaStoreAudioDao()
    private val mediaStoreGenreDao = db.mediaStoreGenreDao()
    private val blacklistDao = db.blacklistDao()
    private val sut = db.mediaStoreGenresDao()
    private val sortRepository = SortRepository(db.sortDao())

    @Test
    fun testGetAll() = runTest {
        blacklistDao.insertAll(BlacklistEntity("blacklisted"))
        mediaStoreAudioDao.insertAll(
            emptyMediaStoreAudioEntity(id = "10", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "20", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "30", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "40", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "50", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "60", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "70", isPodcast = false, directory = "blacklisted"),
        )

        mediaStoreGenreDao.insertAllGenres(
            emptyMediaStoreGenreEntity(id = "1"),
            emptyMediaStoreGenreEntity(id = "2"),
            emptyMediaStoreGenreEntity(id = "no matching songs"),
        )
        mediaStoreGenreDao.insertAllGenreTracks(
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "30"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "40"), // this is a podcast, should be skipped
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "50"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "60"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "70"), // this is blacklisted, should be skipped
        )

        val expected = listOf(
            emptyMediaStoreGenresView(id = "1", songs = 3), // ids: 10, 20, 30
            emptyMediaStoreGenresView(id = "2", songs = 2), // ids: 50, 60
        )

        Assert.assertEquals(
            expected,
            sut.getAll()
        )
    }

    @Test
    fun testGetAllSortedByTitleAsc() = runTest {
        sortRepository.setAllGenresSort(AllGenresSort(GenreSortType.Name, SortDirection.ASCENDING))

        blacklistDao.insertAll(BlacklistEntity("blacklisted"))
        mediaStoreAudioDao.insertAll(
            emptyMediaStoreAudioEntity(id = "10", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "20", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "30", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "40", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "50", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "60", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "70", isPodcast = false, directory = "blacklisted"),
        )

        mediaStoreGenreDao.insertAllGenres(
            emptyMediaStoreGenreEntity(id = "1", name = "déh"),
            emptyMediaStoreGenreEntity(id = "2", name = "dEa"),
            emptyMediaStoreGenreEntity(id = "3", name = "dèb"),
            emptyMediaStoreGenreEntity(id = "no matching songs"),
        )
        mediaStoreGenreDao.insertAllGenreTracks(
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "30"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "40"), // this is a podcast, should be skipped
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "50"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "60"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "70"), // this is blacklisted, should be skipped
            emptyMediaStoreGenreTrackEntity(genreId = "3", songId = "10"),
        )

        val expected = listOf(
            emptyMediaStoreGenresViewSorted(id = "2", name = "dEa", songs = 2),
            emptyMediaStoreGenresViewSorted(id = "3", name = "dèb", songs = 1),
            emptyMediaStoreGenresViewSorted(id = "1", name = "déh", songs = 3),
        )

        Assert.assertEquals(
            expected,
            sut.getAllSorted()
        )
    }

    @Test
    fun testGetAllSortedByTitleDesc() = runTest {
        sortRepository.setAllGenresSort(AllGenresSort(GenreSortType.Name, SortDirection.DESCENDING))

        blacklistDao.insertAll(BlacklistEntity("blacklisted"))
        mediaStoreAudioDao.insertAll(
            emptyMediaStoreAudioEntity(id = "10", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "20", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "30", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "40", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "50", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "60", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "70", isPodcast = false, directory = "blacklisted"),
        )

        mediaStoreGenreDao.insertAllGenres(
            emptyMediaStoreGenreEntity(id = "1", name = "déh"),
            emptyMediaStoreGenreEntity(id = "2", name = "dEa"),
            emptyMediaStoreGenreEntity(id = "3", name = "dèb"),
            emptyMediaStoreGenreEntity(id = "no matching songs"),
        )
        mediaStoreGenreDao.insertAllGenreTracks(
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "30"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "40"), // this is a podcast, should be skipped
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "50"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "60"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "70"), // this is blacklisted, should be skipped
            emptyMediaStoreGenreTrackEntity(genreId = "3", songId = "10"),
        )

        val expected = listOf(
            emptyMediaStoreGenresViewSorted(id = "1", name = "déh", songs = 3),
            emptyMediaStoreGenresViewSorted(id = "3", name = "dèb", songs = 1),
            emptyMediaStoreGenresViewSorted(id = "2", name = "dEa", songs = 2),
        )

        Assert.assertEquals(
            expected,
            sut.getAllSorted()
        )
    }

}