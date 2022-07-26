package dev.olog.data.mediastore

import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.SongSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DatabaseTest
import dev.olog.data.SortedData
import dev.olog.data.blacklist.db.BlacklistEntity
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreSongView
import dev.olog.data.emptyMediaStoreSortedSongView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStoreSongViewDaoTest : DatabaseTest() {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val blacklistDao = db.blacklistDao()
    private val sortRepository = SortRepository(db.sortDao())
    private val sut = db.mediaStoreSongDao()

    @Before
    fun setup() = runTest {
        val blacklisted = "blacklisted"
        mediaStoreDao.insertAll(
            listOf(
                // blacklisted song
                emptyMediaStoreAudioEntity(
                    id = "-1",
                    directory = blacklisted,
                    isPodcast = false
                ),
                // blacklisted podcast
                emptyMediaStoreAudioEntity(
                    id = "-2",
                    directory = blacklisted,
                    isPodcast = true
                ),
                // non blacklisted podcast
                emptyMediaStoreAudioEntity(
                    id = "-3",
                    directory = "non blacklisted",
                    isPodcast = true
                ),
            )
        )
        blacklistDao.insertAll(listOf(BlacklistEntity(blacklisted)))
    }

    @Test
    fun testGetAll() = runTest {
        mediaStoreDao.insertAll(
            SortedData.title.map { emptyMediaStoreAudioEntity(id = it.id, title = it.value, isPodcast = false) }
        )

        val expected = SortedData.titleSortedAsc.map {
            emptyMediaStoreSongView(id = it.id, title = it.value, isPodcast = false)
        }
        val actual = sut.getAll()

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByTitleAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Title, SortDirection.ASCENDING))
        testSort(
            nonSorted = SortedData.title,
            sorted = SortedData.titleSortedAsc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, title = it.value, isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, title = it.value, isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByTitleDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Title, SortDirection.DESCENDING))
        testSort(
            nonSorted = SortedData.title,
            sorted = SortedData.titleSortedDesc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, title = it.value, isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, title = it.value, isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByArtistAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Artist, SortDirection.ASCENDING))
        testSort(
            nonSorted = SortedData.artist,
            sorted = SortedData.artistSortedAsc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, artist = it.value, title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, artist = it.value, title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByArtistDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Artist, SortDirection.DESCENDING))
        testSort(
            nonSorted = SortedData.artist,
            sorted = SortedData.artistSortedDesc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, artist = it.value, title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, artist = it.value, title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByAlbumAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Album, SortDirection.ASCENDING))
        testSort(
            nonSorted = SortedData.album,
            sorted = SortedData.albumSortedAsc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, album = it.value, title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, album = it.value, title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByAlbumDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Album, SortDirection.DESCENDING))
        testSort(
            nonSorted = SortedData.album,
            sorted = SortedData.albumSortedDesc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, album = it.value, title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, album = it.value, title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByDurationAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Duration, SortDirection.ASCENDING))
        testSort(
            nonSorted = SortedData.duration,
            sorted = SortedData.durationSortedAsc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, duration = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, duration = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByDurationDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Duration, SortDirection.DESCENDING))
        testSort(
            nonSorted = SortedData.duration,
            sorted = SortedData.durationSortedDesc,
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, duration = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, duration = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByDateAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Date, SortDirection.ASCENDING))
        testSort(
            nonSorted = SortedData.date,
            sorted = SortedData.dateSortedAsc, // is inverted
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, dateAdded = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, dateAdded = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    @Test
    fun testGetAllSortedByDateDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Date, SortDirection.DESCENDING))
        testSort(
            nonSorted = SortedData.date,
            sorted = SortedData.dateSortedDesc, // is inverted
            nonSortedFactory = { emptyMediaStoreAudioEntity(id = it.id, dateAdded = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) },
            sortedFactory = { emptyMediaStoreSortedSongView(id = it.id, dateAdded = it.value.toLong(), title = it.value2.orEmpty(), isPodcast = false) }
        )
    }

    private suspend fun testSort(
        nonSorted: List<SortedData>,
        sorted: List<SortedData>,
        nonSortedFactory: (SortedData) -> MediaStoreAudioEntity,
        sortedFactory: (SortedData) -> MediaStoreSortedSongView,
    ) {
        mediaStoreDao.insertAll(nonSorted.map { nonSortedFactory(it) })

        val expected = sorted.map { sortedFactory(it) }.convert()
        val actual = sut.getSortedAll().convert()

        Assert.assertEquals(expected, actual)
    }

    private fun List<MediaStoreSortedSongView>.convert(): List<SortedSong> {
        return map {
            SortedSong(
                id = it.id,
                title = it.title,
                artist = it.artist,
                album = it.album,
                duration = it.duration,
                date = it.dateAdded,
            )
        }
    }

    data class SortedSong(
        val id: String,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,
        val date: Long,
    )

}