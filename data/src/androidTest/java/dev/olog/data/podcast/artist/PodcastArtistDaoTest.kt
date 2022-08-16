package dev.olog.data.podcast.artist

import dev.olog.core.Quadruple
import dev.olog.core.entity.sort.AllPodcastArtistsSort
import dev.olog.core.entity.sort.PodcastArtistEpisodesSort
import dev.olog.core.entity.sort.PodcastArtistEpisodesSortType
import dev.olog.core.entity.sort.PodcastArtistSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DataConstants
import dev.olog.data.DatabaseTest
import dev.olog.data.TestData
import dev.olog.data.emptyMediaStorePodcastArtistSortedView
import dev.olog.data.emptyMediaStorePodcastArtistView
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.podcast.artist.MediaStorePodcastArtistsView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PodcastArtistDaoTest : DatabaseTest() {

    private val sortRepository = SortRepository(db.sortDao())
    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.podcastArtistDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStorePodcastArtistsViewDaoTest
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", artistId = "artistId 10", artist = "artist 1", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "id 2", artistId = "artistId 10", artist = "artist 1", isPodcast = true),
            emptyMediaStoreAudioEntity(id = "id 3", artistId = "artistId 20", artist = "artist 2", isPodcast = true),
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllPodcastArtistsSort(PodcastArtistSortType.Name, SortDirection.ASCENDING)
        sortRepository.setAllPodcastArtistsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastArtistSortedView(id = "artistId 10", name = "artist 1", songs = 2),
            emptyMediaStorePodcastArtistSortedView(id = "artistId 20", name = "artist 2", songs = 1),
        )

        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllPodcastArtistsSort(PodcastArtistSortType.Name, SortDirection.ASCENDING)
        SortRepository(db.sortDao()).setAllPodcastArtistsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastArtistSortedView(id = "artistId 10", name = "artist 1", songs = 2),
            emptyMediaStorePodcastArtistSortedView(id = "artistId 20", name = "artist 2", songs = 1),
        )

        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() = runTest {
        Assert.assertEquals(
            emptyMediaStorePodcastArtistView(id = "artistId 10", name = "artist 1", songs = 2),
            sut.getById("artistId 10"),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastArtistView(id = "artistId 20", name = "artist 2", songs = 1),
            sut.getById("artistId 20"),
        )
        Assert.assertEquals(
            null,
            sut.getById("artistId 30"),
        )
    }

    @Test
    fun testObserveById() = runTest {
        Assert.assertEquals(
            emptyMediaStorePodcastArtistView(id = "artistId 10", name = "artist 1", songs = 2),
            sut.observeById("artistId 10").first(),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastArtistView(id = "artistId 20", name = "artist 2", songs = 1),
            sut.observeById("artistId 20").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("artistId 30").first(),
        )
    }

    @Test
    fun testGetTracksByIdSortedByTitleAsc() = runTest {
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Title, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            "211" to "dEa",
            "201" to "dèb",
            "200" to "dec",
            "210" to "déh",
            "212" to "ggg",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { it.id to it.title },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { it.id to it.title },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Title, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            "212" to "ggg",
            "210" to "déh",
            "200" to "dec",
            "201" to "dèb",
            "211" to "dEa",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { it.id to it.title },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { it.id to it.title },
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
    fun testGetTracksByIdSortedByAlbumAsc() = runTest {
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Album, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("211", "dec album 2", "dEa"),
            Triple("210", "dec album 2", "déh"),
            Triple("212", "dec album 2", "ggg"),
            Triple("201", "déh album 1", "dèb"),
            Triple("200", "déh album 1", "dec"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.album, it.title) },
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
    fun testGetTracksByIdSortedByAlbumDesc() = runTest {
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Album, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("200", "déh album 1", "dec"),
            Triple("201", "déh album 1", "dèb"),
            Triple("212", "dec album 2", "ggg"),
            Triple("210", "dec album 2", "déh"),
            Triple("211", "dec album 2", "dEa"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.album, it.title) },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Duration, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("211", 200L, "dEa"),
            Triple("201", 200L, "dèb"),
            Triple("200", 200L, "dec"),
            Triple("210", 200L, "déh"),
            Triple("212", 210L, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.duration, it.title) },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Duration, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("212", 210L, "ggg"),
            Triple("210", 200L, "déh"),
            Triple("200", 200L, "dec"),
            Triple("201", 200L, "dèb"),
            Triple("211", 200L, "dEa"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.duration, it.title) },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("212", 211L, "ggg"),
            Triple("211", 201L, "dEa"),
            Triple("201", 201L, "dèb"),
            Triple("200", 201L, "dec"),
            Triple("210", 201L, "déh"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.dateAdded, it.title) },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Triple("210", 201L, "déh"),
            Triple("200", 201L, "dec"),
            Triple("201", 201L, "dèb"),
            Triple("211", 201L, "dEa"),
            Triple("212", 211L, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.getTracksById("missing").map { it.id to it.title },
        )
        Assert.assertEquals(
            emptyList<MediaStorePodcastsView>(),
            sut.observeTracksById("missing").first().map { it.id to it.title },
        )
    }

    @Test
    fun testGetTracksByIdSortedByTrackNumberAsc() = runTest {
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.TrackNumber, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Quadruple("200", 1, 1, "dec"),
            Quadruple("201", 1, 2, "dèb"),
            Quadruple("210", 2, 1, "déh"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("212", 2, 3, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
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
        sortRepository.setPodcastArtistEpisodesSort(PodcastArtistEpisodesSort(PodcastArtistEpisodesSortType.TrackNumber, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(true))

        val expected = listOf(
            Quadruple("212", 2, 3, "ggg"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("210", 2, 1, "déh"),
            Quadruple("201", 1, 2, "dèb"),
            Quadruple("200", 1, 1, "dec"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("2").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("2").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
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
        sut.insertLastPlayed(LastPlayedPodcastArtistEntity(id = 1, dateAdded = 100))
        sut.insertLastPlayed(LastPlayedPodcastArtistEntity(id = 1, dateAdded = 110))
        sut.insertLastPlayed(LastPlayedPodcastArtistEntity(id = 2, dateAdded = 200))
    }

    @Test
    fun testLastPlayedLimit() = runTest {
        Assert.assertEquals(
            emptyList<MediaStorePodcastArtistsView>(),
            sut.observeLastPlayed().first()
        )

        val total = DataConstants.MAX_LAST_PLAYED * 2
        for (index in 0 until total) {
            mediaStoreDao.insertAll(
                emptyMediaStoreAudioEntity(id = "song $index", artistId = index.toString(), isPodcast = false),
                emptyMediaStoreAudioEntity(id = "podcast $index", artistId = index.toString(), isPodcast = true),
            )
            sut.insertLastPlayed(LastPlayedPodcastArtistEntity(index.toLong(), (index * 10).toLong()))
        }

        val expected = (1 .. DataConstants.MAX_LAST_PLAYED).map { index ->
            emptyMediaStorePodcastArtistView(id = (total - index).toString(), songs = 1)
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
                artistId = "10",
                isPodcast = true,
                dateAdded = now
            ),
            // added now, but is song, should be skipped
            emptyMediaStoreAudioEntity(
                id = "2",
                artistId = "20",
                isPodcast = false,
                dateAdded = now
            ),
            // added with 0 time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "3",
                artistId = "30",
                isPodcast = true,
                dateAdded = 0,
            ),
            // exact expiration time, should be skipped
            emptyMediaStoreAudioEntity(
                id = "4",
                artistId = "40",
                isPodcast = true,
                dateAdded = exactExpirationTime,
            ),
            // expiration time and 1 second, should be skipped
            emptyMediaStoreAudioEntity(
                id = "5",
                artistId = "50",
                isPodcast = true,
                dateAdded = exactExpirationTime - oneSecond,
            ),
            // expiration time minus 1 second, should be included
            emptyMediaStoreAudioEntity(
                id = "6",
                artistId = "60",
                isPodcast = true,
                dateAdded = exactExpirationTime + oneSecond,
            ),
            // somewhere in between not and expiration
            emptyMediaStoreAudioEntity(
                id = "7",
                artistId = "70",
                isPodcast = true,
                dateAdded = halfExpiration,
            ),
            // 2 artists with different added time, get the lowest
            emptyMediaStoreAudioEntity(
                id = "8",
                artistId = "80",
                isPodcast = true,
                dateAdded = halfExpiration + oneSecond,
            ),
            emptyMediaStoreAudioEntity(
                id = "9",
                artistId = "80",
                isPodcast = true,
                dateAdded = halfExpiration - oneSecond,
            ),
        )


        Assert.assertEquals(
            listOf(
                emptyMediaStorePodcastArtistView(id = "10", songs = 1, dateAdded = now),
                emptyMediaStorePodcastArtistView(id = "70", songs = 1, dateAdded = halfExpiration),
                emptyMediaStorePodcastArtistView(id = "80", songs = 2, dateAdded = halfExpiration - oneSecond),
                emptyMediaStorePodcastArtistView(id = "60", songs = 1, dateAdded = exactExpirationTime + oneSecond),
            ),
            sut.observeRecentlyAdded().first()
        )
    }

}