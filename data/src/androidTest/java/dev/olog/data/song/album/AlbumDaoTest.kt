package dev.olog.data.song.album

import dev.olog.core.Quadruple
import dev.olog.core.entity.sort.AlbumSongsSort
import dev.olog.core.entity.sort.AlbumSongsSortType
import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DataConstants
import dev.olog.data.DatabaseTest
import dev.olog.data.TestData
import dev.olog.data.emptyMediaStoreAlbumSortedView
import dev.olog.data.emptyMediaStoreAlbumView
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.album.MediaStoreAlbumsView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class AlbumDaoTest : DatabaseTest() {

    private val sortRepository = SortRepository(db.sortDao())
    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.albumDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStoreAlbumsViewDaoTest
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", albumId = "albumId 10", album = "album 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 2", albumId = "albumId 10", album = "album 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 3 ", albumId = "albumId 20", album = "album 2", isPodcast = false),
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllAlbumsSort(AlbumSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllAlbumsSort(sort)

        val expected = listOf(
            emptyMediaStoreAlbumSortedView(id = "albumId 10", title = "album 1", songs = 2),
            emptyMediaStoreAlbumSortedView(id = "albumId 20", title = "album 2", songs = 1),
        )

        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllAlbumsSort(AlbumSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllAlbumsSort(sort)

        val expected = listOf(
            emptyMediaStoreAlbumSortedView(id = "albumId 10", title = "album 1", songs = 2),
            emptyMediaStoreAlbumSortedView(id = "albumId 20", title = "album 2", songs = 1),
        )

        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() = runTest {
        Assert.assertEquals(
            emptyMediaStoreAlbumView(id = "albumId 10", title = "album 1", songs = 2),
            sut.getById("albumId 10"),
        )
        Assert.assertEquals(
            emptyMediaStoreAlbumView(id = "albumId 20", title = "album 2", songs = 1),
            sut.getById("albumId 20"),
        )
        Assert.assertEquals(
            null,
            sut.getById("albumId 30"),
        )
    }

    @Test
    fun testObserveById() = runTest {
        Assert.assertEquals(
            emptyMediaStoreAlbumView(id = "albumId 10", title = "album 1", songs = 2),
            sut.observeById("albumId 10").first(),
        )
        Assert.assertEquals(
            emptyMediaStoreAlbumView(id = "albumId 20", title = "album 2", songs = 1),
            sut.observeById("albumId 20").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("albumId 30").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTitleAsc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Title, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "211" to "dEa",
            "210" to "déh",
            "212" to "ggg",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { it.id to it.title },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { it.id to it.title },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTitleDesc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Title, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "212" to "ggg",
            "210" to "déh",
            "211" to "dEa",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { it.id to it.title },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { it.id to it.title },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByAlbumArtistAsc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.AlbumArtist, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("211", "dec album artist 2", "dEa"),
            Triple("210", "dec album artist 2", "déh"),
            Triple("212", "dec album artist 2", "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByAlbumArtistDesc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.AlbumArtist, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("212", "dec album artist 2", "ggg"),
            Triple("210", "dec album artist 2", "déh"),
            Triple("211", "dec album artist 2", "dEa"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDurationAsc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Duration, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("211", 200L, "dEa"),
            Triple("210", 200L, "déh"),
            Triple("212", 210L, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDurationDesc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Duration, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("212", 210L, "ggg"),
            Triple("210", 200L, "déh"),
            Triple("211", 200L, "dEa"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDateAsc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("212", 211L, "ggg"),
            Triple("211", 201L, "dEa"),
            Triple("210", 201L, "déh"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDateDesc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("210", 201L, "déh"),
            Triple("211", 201L, "dEa"),
            Triple("212", 211L, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTrackNumberAsc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.TrackNumber, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Quadruple("210", 2, 1, "déh"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("212", 2, 3, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTrackNumberDesc() = runTest {
        sortRepository.setAlbumSongsSort(AlbumSongsSort(AlbumSongsSortType.TrackNumber, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Quadruple("212", 2, 3, "ggg"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("210", 2, 1, "déh"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("21").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("21").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testLastPlayedConflicts() = runTest {
        sut.insertLastPlayed(LastPlayedAlbumEntity(id = 1, dateAdded = 100))
        sut.insertLastPlayed(LastPlayedAlbumEntity(id = 1, dateAdded = 110))
        sut.insertLastPlayed(LastPlayedAlbumEntity(id = 2, dateAdded = 200))
    }

    @Test
    fun testLastPlayedLimit() = runTest {
        Assert.assertEquals(
            emptyList<MediaStoreAlbumsView>(),
            sut.observeLastPlayed().first()
        )

        val total = DataConstants.MAX_LAST_PLAYED * 2
        for (index in 0 until total) {
            mediaStoreDao.insertAll(
                emptyMediaStoreAudioEntity(id = "song $index", albumId = index.toString(), isPodcast = false),
                emptyMediaStoreAudioEntity(id = "podcast $index", albumId = index.toString(), isPodcast = true),
            )
            sut.insertLastPlayed(LastPlayedAlbumEntity(index.toLong(), (index * 10).toLong()))
        }

        val expected = (1..DataConstants.MAX_LAST_PLAYED).map { index ->
            emptyMediaStoreAlbumView(id = (total - index).toString(), songs = 1)
        }
        Assert.assertEquals(DataConstants.MAX_LAST_PLAYED, expected.size)

        Assert.assertEquals(
            expected,
            sut.observeLastPlayed().first()
        )
    }

    @Test
    fun testObserveRecentlyAdded() = runTest {
        val now = System.currentTimeMillis().milliseconds.inWholeSeconds
        val exactExpirationTime = now - DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS.seconds.inWholeSeconds
        val oneSecond = 1.seconds.inWholeMilliseconds
        val halfExpiration = now - (DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS.seconds.inWholeSeconds / 2)

        mediaStoreDao.insertAll(
            // added now
            emptyMediaStoreAudioEntity(
                id = "1",
                albumId = "10",
                isPodcast = false,
                dateAdded = now
            ),
            // added now, but is podcast, should be skipped
            emptyMediaStoreAudioEntity(
                id = "2",
                albumId = "20",
                isPodcast = true,
                dateAdded = now
            ),
            // added with 0 time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "3",
                albumId = "30",
                isPodcast = false,
                dateAdded = 0,
            ),
            // exact expiration time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "4",
                albumId = "40",
                isPodcast = false,
                dateAdded = exactExpirationTime,
            ),
            // expiration time and 1 second, should be skipped
            emptyMediaStoreAudioEntity(
                id = "5",
                albumId = "50",
                isPodcast = false,
                dateAdded = exactExpirationTime - oneSecond,
            ),
            // expiration time minus 1 second, should be included
            emptyMediaStoreAudioEntity(
                id = "6",
                albumId = "60",
                isPodcast = false,
                dateAdded = exactExpirationTime + oneSecond,
            ),
            // somewhere in between not and expiration
            emptyMediaStoreAudioEntity(
                id = "7",
                albumId = "70",
                isPodcast = false,
                dateAdded = halfExpiration,
            ),
            // 2 albums with different added time, get the lowest
            emptyMediaStoreAudioEntity(
                id = "8",
                albumId = "80",
                isPodcast = false,
                dateAdded = halfExpiration + oneSecond,
            ),
            emptyMediaStoreAudioEntity(
                id = "9",
                albumId = "80",
                isPodcast = false,
                dateAdded = halfExpiration - oneSecond,
            ),
        )


        Assert.assertEquals(
            listOf(
                emptyMediaStoreAlbumView(id = "10", songs = 1, dateAdded = now),
                emptyMediaStoreAlbumView(id = "70", songs = 1, dateAdded = halfExpiration),
                emptyMediaStoreAlbumView(id = "80", songs = 2, dateAdded = halfExpiration - oneSecond),
                emptyMediaStoreAlbumView(id = "60", songs = 1, dateAdded = exactExpirationTime + oneSecond),
            ),
            sut.observeRecentlyAdded().first()
        )
    }

    @Test
    fun testObserveSiblings() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", artistId = "10", albumId = "100", isPodcast = true), // should skip
            emptyMediaStoreAudioEntity(id = "2", artistId = "11", albumId = "101", isPodcast = false), // should skip
            emptyMediaStoreAudioEntity(id = "3", artistId = "10", albumId = "102", album = "déh", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "4", artistId = "10", albumId = "102", album = "déh", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "5", artistId = "10", albumId = "103", album = "dèb", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "6", artistId = "10", albumId = "104", album = "dEa", isPodcast = false),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreAlbumView(id = "104", artistId = "10", title = "dEa", songs = 1),
                emptyMediaStoreAlbumView(id = "103", artistId = "10", title = "dèb", songs = 1),
            ),
            sut.observeSiblings("102").first()
        )
    }

    @Test
    fun testObserveArtistAlbums() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", artistId = "10", albumId = "100", isPodcast = true), // should skip
            emptyMediaStoreAudioEntity(id = "2", artistId = "11", albumId = "101", isPodcast = false), // should skip
            emptyMediaStoreAudioEntity(id = "3", artistId = "10", albumId = "102", album = "déh", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "4", artistId = "10", albumId = "102", album = "déh", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "5", artistId = "10", albumId = "103", album = "dèb", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "6", artistId = "10", albumId = "104", album = "dEa", isPodcast = false),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreAlbumView(id = "104", artistId = "10", title = "dEa", songs = 1),
                emptyMediaStoreAlbumView(id = "103", artistId = "10", title = "dèb", songs = 1),
                emptyMediaStoreAlbumView(id = "102", artistId = "10", title = "déh", songs = 2),
            ),
            sut.observeArtistAlbums("10").first()
        )
    }

}