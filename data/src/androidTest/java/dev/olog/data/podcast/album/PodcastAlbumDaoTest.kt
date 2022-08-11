package dev.olog.data.podcast.album

import dev.olog.core.Quadruple
import dev.olog.core.entity.sort.AllPodcastAlbumsSort
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSort
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSortType
import dev.olog.core.entity.sort.PodcastAlbumSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DataConstants
import dev.olog.data.DatabaseTest
import dev.olog.data.TestData
import dev.olog.data.emptyMediaStorePodcastAlbumSortedView
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStorePodcastAlbumView
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.podcast.album.MediaStorePodcastAlbumsView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PodcastAlbumDaoTest : DatabaseTest() {

    private val sortRepository = SortRepository(db.sortDao())
    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.podcastAlbumDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStorePodcastAlbumsViewDaoTest
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", albumId = "albumId 10", album = "album 1", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "id 2", albumId = "albumId 10", album = "album 1", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "id 3 ", albumId = "albumId 20", album = "album 2", isPodcast = true),
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllPodcastAlbumsSort(PodcastAlbumSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllPodcastAlbumsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastAlbumSortedView(id = "albumId 10", title = "album 1", songs = 2),
            emptyMediaStorePodcastAlbumSortedView(id = "albumId 20", title = "album 2", songs = 1),
        )

        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllPodcastAlbumsSort(PodcastAlbumSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllPodcastAlbumsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastAlbumSortedView(id = "albumId 10", title = "album 1", songs = 2),
            emptyMediaStorePodcastAlbumSortedView(id = "albumId 20", title = "album 2", songs = 1),
        )

        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() = runTest {
        Assert.assertEquals(
            emptyMediaStorePodcastAlbumView(id = "albumId 10", title = "album 1", songs = 2),
            sut.getById("albumId 10"),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastAlbumView(id = "albumId 20", title = "album 2", songs = 1),
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
            emptyMediaStorePodcastAlbumView(id = "albumId 10", title = "album 1", songs = 2),
            sut.observeById("albumId 10").first(),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastAlbumView(id = "albumId 20", title = "album 2", songs = 1),
            sut.observeById("albumId 20").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("albumId 30").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTitleAsc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Title, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTitleDesc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Title, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDurationAsc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Duration, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDurationDesc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Duration, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDateAsc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByDateDesc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTrackNumberAsc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.TrackNumber, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTrackNumberDesc() = runTest {
        sortRepository.setPodcastAlbumEpisodesSort(PodcastAlbumEpisodesSort(PodcastAlbumEpisodesSortType.TrackNumber, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

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
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first(),
        )
    }

    @Test
    fun testLastPlayedConflicts() = runTest {
        sut.insertLastPlayed(LastPlayedPodcastAlbumEntity(id = 1, dateAdded = 100))
        sut.insertLastPlayed(LastPlayedPodcastAlbumEntity(id = 1, dateAdded = 110))
        sut.insertLastPlayed(LastPlayedPodcastAlbumEntity(id = 2, dateAdded = 200))
    }

    @Test
    fun testLastPlayedLimit() = runTest {
        Assert.assertEquals(
            emptyList<MediaStorePodcastAlbumsView>(),
            sut.observeLastPlayed().first()
        )

        val total = DataConstants.MAX_LAST_PLAYED * 2
        for (index in 0 until total) {
            mediaStoreDao.insertAll(
                emptyMediaStoreAudioEntity(id = "song $index", albumId = index.toString(), isPodcast = false),
                emptyMediaStoreAudioEntity(id = "podcast $index", albumId = index.toString(), isPodcast = true),
            )
            sut.insertLastPlayed(LastPlayedPodcastAlbumEntity(index.toLong(), (index * 10).toLong()))
        }

        val expected = (1..DataConstants.MAX_LAST_PLAYED).map { index ->
            emptyMediaStorePodcastAlbumView(id = (total - index).toString(), songs = 1)
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
                isPodcast = true,
                dateAdded = now
            ),
            // added now, but is song, should be skipped
            emptyMediaStoreAudioEntity(
                id = "2",
                albumId = "20",
                isPodcast = false,
                dateAdded = now
            ),
            // added with 0 time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "3",
                albumId = "30",
                isPodcast = true,
                dateAdded = 0,
            ),
            // exact expiration time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "4",
                albumId = "40",
                isPodcast = true,
                dateAdded = exactExpirationTime,
            ),
            // expiration time and 1 second, should be skipped
            emptyMediaStoreAudioEntity(
                id = "5",
                albumId = "50",
                isPodcast = true,
                dateAdded = exactExpirationTime - oneSecond,
            ),
            // expiration time minus 1 second, should be included
            emptyMediaStoreAudioEntity(
                id = "6",
                albumId = "60",
                isPodcast = true,
                dateAdded = exactExpirationTime + oneSecond,
            ),
            // somewhere in between not and expiration
            emptyMediaStoreAudioEntity(
                id = "7",
                albumId = "70",
                isPodcast = true,
                dateAdded = halfExpiration,
            ),
            // 2 albums with different added time, get the lowest
            emptyMediaStoreAudioEntity(
                id = "8",
                albumId = "80",
                isPodcast = true,
                dateAdded = halfExpiration + oneSecond,
            ),
            emptyMediaStoreAudioEntity(
                id = "9",
                albumId = "80",
                isPodcast = true,
                dateAdded = halfExpiration - oneSecond,
            ),
        )


        Assert.assertEquals(
            listOf(
                emptyMediaStorePodcastAlbumView(id = "10", songs = 1, dateAdded = now),
                emptyMediaStorePodcastAlbumView(id = "70", songs = 1, dateAdded = halfExpiration),
                emptyMediaStorePodcastAlbumView(id = "80", songs = 2, dateAdded = halfExpiration - oneSecond),
                emptyMediaStorePodcastAlbumView(id = "60", songs = 1, dateAdded = exactExpirationTime + oneSecond),
            ),
            sut.observeRecentlyAdded().first()
        )
    }

    @Test
    fun testObserveSiblings() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", artistId = "10", albumId = "100", isPodcast = false), // should skip
            emptyMediaStoreAudioEntity(id = "2", artistId = "11", albumId = "101", isPodcast = true), // should skip
            emptyMediaStoreAudioEntity(id = "3", artistId = "10", albumId = "102", album = "déh", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "4", artistId = "10", albumId = "102", album = "déh", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "5", artistId = "10", albumId = "103", album = "dèb", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "6", artistId = "10", albumId = "104", album = "dEa", isPodcast = true),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStorePodcastAlbumView(id = "104", artistId = "10", title = "dEa", songs = 1),
                emptyMediaStorePodcastAlbumView(id = "103", artistId = "10", title = "dèb", songs = 1),
            ),
            sut.observeSiblings("102").first()
        )
    }

    @Test
    fun testObserveArtistAlbums() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", artistId = "10", albumId = "100", isPodcast = false), // should skip
            emptyMediaStoreAudioEntity(id = "2", artistId = "11", albumId = "101", isPodcast = true), // should skip
            emptyMediaStoreAudioEntity(id = "3", artistId = "10", albumId = "102", album = "déh", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "4", artistId = "10", albumId = "102", album = "déh", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "5", artistId = "10", albumId = "103", album = "dèb", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "6", artistId = "10", albumId = "104", album = "dEa", isPodcast = true),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStorePodcastAlbumView(id = "104", artistId = "10", title = "dEa", songs = 1),
                emptyMediaStorePodcastAlbumView(id = "103", artistId = "10", title = "dèb", songs = 1),
                emptyMediaStorePodcastAlbumView(id = "102", artistId = "10", title = "déh", songs = 2),
            ),
            sut.observeArtistAlbums("10").first()
        )
    }

}