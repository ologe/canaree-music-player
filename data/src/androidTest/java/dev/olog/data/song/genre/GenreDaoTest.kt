package dev.olog.data.song.genre

import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.GenreSongsSort
import dev.olog.core.entity.sort.GenreSongsSortType
import dev.olog.core.entity.sort.GenreSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DataConstants
import dev.olog.data.DatabaseTest
import dev.olog.data.TestData
import dev.olog.data.emptyMediaStoreArtistView
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreGenreEntity
import dev.olog.data.emptyMediaStoreGenreTrackEntity
import dev.olog.data.emptyMediaStoreGenresView
import dev.olog.data.emptyMediaStoreGenresViewSorted
import dev.olog.data.emptyMediaStoreSongView
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GenreDaoTest : DatabaseTest() {

    private val sortRepository = SortRepository(db.sortDao())
    private val mediaStoreAudioDao = db.mediaStoreAudioDao()
    private val mediaStoreGenreDao = db.mediaStoreGenreDao()
    private val sut = db.genreDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStoreGenresViewDaoTest
        mediaStoreGenreDao.insertAllGenres(
            emptyMediaStoreGenreEntity(id = "1", name = "genre 1"),
            emptyMediaStoreGenreEntity(id = "2", name = "genre 2"),
        )

        mediaStoreGenreDao.insertAllGenreTracks(
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
            emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "20"),
            emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "30"),
        )

        mediaStoreAudioDao.insertAll(
            emptyMediaStoreAudioEntity(id = "10", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "20", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "30", isPodcast = false),
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllGenresSort(GenreSortType.Name, SortDirection.ASCENDING)
        sortRepository.setAllGenresSort(sort)

        val expected = listOf(
            emptyMediaStoreGenresViewSorted(id = "1", name = "genre 1", songs = 2),
            emptyMediaStoreGenresViewSorted(id = "2", name = "genre 2", songs = 1),
        )

        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllGenresSort(GenreSortType.Name, SortDirection.ASCENDING)
        sortRepository.setAllGenresSort(sort)

        val expected = listOf(
            emptyMediaStoreGenresViewSorted(id = "1", name = "genre 1", songs = 2),
            emptyMediaStoreGenresViewSorted(id = "2", name = "genre 2", songs = 1),
        )

        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() = runTest {
        Assert.assertEquals(
            emptyMediaStoreGenresView(id = "1", name = "genre 1", songs = 2),
            sut.getById("1"),
        )
        Assert.assertEquals(
            emptyMediaStoreGenresView(id = "2", name = "genre 2", songs = 1),
            sut.getById("2"),
        )
        Assert.assertEquals(
            null,
            sut.getById("3"),
        )
    }

    @Test
    fun testObserveById() = runTest {
        Assert.assertEquals(
            emptyMediaStoreGenresView(id = "1", name = "genre 1", songs = 2),
            sut.observeById("1").first(),
        )
        Assert.assertEquals(
            emptyMediaStoreGenresView(id = "2", name = "genre 2", songs = 1),
            sut.observeById("2").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("3").first(),
        )
    }

    @Test
    fun testgetTracksByIdSortedByTitleAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Title, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            "100" to "aaa track",
            "211" to "dEa",
            "201" to "dèb",
            "200" to "dec",
            "300" to "dEG",
            "210" to "déh",
            "212" to "ggg",
            "400" to "hello",
            "101" to "zzz track",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { it.id to it.title },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { it.id to it.title },
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
    fun testgetTracksByIdSortedByTitleDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Title, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            "101" to "zzz track",
            "400" to "hello",
            "212" to "ggg",
            "210" to "déh",
            "300" to "dEG",
            "200" to "dec",
            "201" to "dèb",
            "211" to "dEa",
            "100" to "aaa track",
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { it.id to it.title },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { it.id to it.title },
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
    fun testgetTracksByIdSortedByArtistAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Artist, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("211", "dEa artist 1", "dEa"),
            Triple("201", "dEa artist 1", "dèb"),
            Triple("200", "dEa artist 1", "dec"),
            Triple("210", "dEa artist 1", "déh"),
            Triple("212", "dEa artist 1", "ggg"),
            Triple("400", "dec artist 3", "hello"),
            Triple("300", "déh artist 2", "dEG"),
            Triple("100", "<unknown>", "aaa track"),
            Triple("101", "<unknown>", "zzz track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.artist, it.title) },
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
    fun testgetTracksByIdSortedByArtistDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Artist, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("300", "déh artist 2", "dEG"),
            Triple("400", "dec artist 3", "hello"),
            Triple("212", "dEa artist 1", "ggg"),
            Triple("210", "dEa artist 1", "déh"),
            Triple("200", "dEa artist 1", "dec"),
            Triple("201", "dEa artist 1", "dèb"),
            Triple("211", "dEa artist 1", "dEa"),
            Triple("101", "<unknown>", "zzz track"),
            Triple("100", "<unknown>", "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.artist, it.title) },
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
    fun testgetTracksByIdSortedByAlbumAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Album, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("300", "dEa another album", "dEG"),
            Triple("211", "dec album 2", "dEa"),
            Triple("210", "dec album 2", "déh"),
            Triple("212", "dec album 2", "ggg"),
            Triple("400", "dEg artist 3 album", "hello"),
            Triple("201", "déh album 1", "dèb"),
            Triple("200", "déh album 1", "dec"),
            Triple("100", "<unknown>", "aaa track"),
            Triple("101", "<unknown>", "zzz track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.album, it.title) },
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
    fun testgetTracksByIdSortedByAlbumDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Album, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("200", "déh album 1", "dec"),
            Triple("201", "déh album 1", "dèb"),
            Triple("400", "dEg artist 3 album", "hello"),
            Triple("212", "dec album 2", "ggg"),
            Triple("210", "dec album 2", "déh"),
            Triple("211", "dec album 2", "dEa"),
            Triple("300", "dEa another album", "dEG"),
            Triple("101", "<unknown>", "zzz track"),
            Triple("100", "<unknown>", "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.album, it.title) },
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
    fun testgetTracksByIdSortedByAlbumArtistAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.AlbumArtist, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("300", "dEa another artist album", "dEG"),
            Triple("211", "dec album artist 2", "dEa"),
            Triple("210", "dec album artist 2", "déh"),
            Triple("212", "dec album artist 2", "ggg"),
            Triple("400", "dEg artist 3 album artist", "hello"),
            Triple("201", "déh album artist 1", "dèb"),
            Triple("200", "déh album artist 1", "dec"),
            Triple("100", "<unknown>", "aaa track"),
            Triple("101", "<unknown>", "zzz track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.albumArtist, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.albumArtist, it.title) },
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
    fun testgetTracksByIdSortedByAlbumArtistDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.AlbumArtist, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("200", "déh album artist 1", "dec"),
            Triple("201", "déh album artist 1", "dèb"),
            Triple("400", "dEg artist 3 album artist", "hello"),
            Triple("212", "dec album artist 2", "ggg"),
            Triple("210", "dec album artist 2", "déh"),
            Triple("211", "dec album artist 2", "dEa"),
            Triple("300", "dEa another artist album", "dEG"),
            Triple("101", "<unknown>", "zzz track"),

            Triple("100", "<unknown>", "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.albumArtist, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.albumArtist, it.title) },
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
    fun testgetTracksByIdSortedByDurationAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Duration, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("101", -10000L, "zzz track"),
            Triple("211", 200L, "dEa"),
            Triple("201", 200L, "dèb"),
            Triple("200", 200L, "dec"),
            Triple("210", 200L, "déh"),
            Triple("400", 200L, "hello"),
            Triple("212", 210L, "ggg"),
            Triple("300", 300L, "dEG"),
            Triple("100", 10000L, "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.duration, it.title) },
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
    fun testgetTracksByIdSortedByDurationDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Duration, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("100", 10000L, "aaa track"),
            Triple("300", 300L, "dEG"),
            Triple("212", 210L, "ggg"),
            Triple("400", 200L, "hello"),
            Triple("210", 200L, "déh"),
            Triple("200", 200L, "dec"),
            Triple("201", 200L, "dèb"),
            Triple("211", 200L, "dEa"),
            Triple("101", -10000L, "zzz track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.duration, it.title) },
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
    fun testgetTracksByIdSortedByDateAsc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Date, SortDirection.ASCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("100", 10001L, "aaa track"),
            Triple("300", 301L, "dEG"),
            Triple("212", 211L, "ggg"),
            Triple("211", 201L, "dEa"),
            Triple("201", 201L, "dèb"),
            Triple("200", 201L, "dec"),
            Triple("210", 201L, "déh"),
            Triple("400", 201L, "hello"),
            Triple("101", -10001L, "zzz track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.dateAdded, it.title) },
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
    fun testgetTracksByIdSortedByDateDesc() = runTest {
        sortRepository.setGenreSongsSort(GenreSongsSort(GenreSongsSortType.Date, SortDirection.DESCENDING))
        setupSongsData()

        val expected = listOf(
            Triple("101", -10001L, "zzz track"),
            Triple("400", 201L, "hello"),
            Triple("210", 201L, "déh"),
            Triple("200", 201L, "dec"),
            Triple("201", 201L, "dèb"),
            Triple("211", 201L, "dEa"),
            Triple("212", 211L, "ggg"),
            Triple("300", 301L, "dEG"),
            Triple("100", 10001L, "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksById("1").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksById("1").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksById("missing").map { it.id to it.title },
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksById("missing").first().map { it.id to it.title },
        )
    }

    @Test
    fun testObserveSiblings() = runTest {
        mediaStoreAudioDao.replaceAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "10", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "20", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "30", isPodcast = false),
            )
        )
        mediaStoreGenreDao.replaceAll(
            genres = listOf(
                emptyMediaStoreGenreEntity(id = "1", name = "déh"),
                emptyMediaStoreGenreEntity(id = "2", name = "dEa"),
                emptyMediaStoreGenreEntity(id = "3", name = "dèb"),
                emptyMediaStoreGenreEntity(id = "4"),
                emptyMediaStoreGenreEntity(id = "no matching songs"),
            ),
            genresTracks = listOf(
                emptyMediaStoreGenreTrackEntity(genreId = "1", songId = "10"),
                emptyMediaStoreGenreTrackEntity(genreId = "2", songId = "20"),
                emptyMediaStoreGenreTrackEntity(genreId = "3", songId = "30"),
            ),
        )

        val expected = listOf(
            emptyMediaStoreGenresView(id = "2", name = "dEa", songs = 1),
            emptyMediaStoreGenresView(id = "3", name = "dèb", songs = 1),
            emptyMediaStoreGenresView(id = "1", name = "déh", songs = 1),
        )

        Assert.assertEquals(
            expected,
            sut.observeSiblings("4").first()
        )
    }

    @Test
    fun testMostPlayed() = runTest {
        mediaStoreAudioDao.replaceAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "id 1", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "id 2", isPodcast = false),
            )
        )
        mediaStoreGenreDao.replaceAll(
            genres = emptyList(),
            genresTracks = listOf(
                emptyMediaStoreGenreTrackEntity("1", "id 1"),
                emptyMediaStoreGenreTrackEntity("1", "id 2"),
            )
        )

        // initial
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeMostPlayed("1").first()
        )

//        // add one less than needed
        (0 until DataConstants.MIN_MOST_PLAYED_TIMES - 1).forEach {
            sut.insertMostPlayed("1", "id 1")
        }

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeMostPlayed("1").first()
        )

//        // add enough for min
        sut.insertMostPlayed("1", "id 1")
        Assert.assertEquals(
            listOf(emptyMediaStoreSongView(id = "id 1", isPodcast = false),),
            sut.observeMostPlayed("1").first()
        )

//        // add another more
        (0 until DataConstants.MIN_MOST_PLAYED_TIMES * 2).forEach {
            sut.insertMostPlayed("1", "id 2")
        }

        Assert.assertEquals(
            listOf(
                emptyMediaStoreSongView(id = "id 2", isPodcast = false),
                emptyMediaStoreSongView(id = "id 1", isPodcast = false),
            ),
            sut.observeMostPlayed("1").first()
        )
    }

    @Test
    fun testObserveRelatedArtists() = runTest {
        mediaStoreGenreDao.replaceAll(
            genres = emptyList(),
            genresTracks = listOf(
                emptyMediaStoreGenreTrackEntity("1", "id 1"),
                emptyMediaStoreGenreTrackEntity("1", "id 2"),
                emptyMediaStoreGenreTrackEntity("1", "id 3"),
                emptyMediaStoreGenreTrackEntity("1", "id 4"),
                emptyMediaStoreGenreTrackEntity("1", "id 5"),
                emptyMediaStoreGenreTrackEntity("2", "id 6"),
            )
        )

        mediaStoreAudioDao.replaceAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "id 1", artistId = "artistId 10", artist = "déh", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "id 2", artistId = "artistId 10", artist = "déh", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "id 3", artistId = "artistId 20", artist = "dEa", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "id 4", artistId = "artistId 30", artist = "dèb", isPodcast = false),
            )
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreArtistView(id = "artistId 20", name = "dEa", songs = 1),
                emptyMediaStoreArtistView(id = "artistId 30", name = "dèb", songs = 1),
                emptyMediaStoreArtistView(id = "artistId 10", name = "déh", songs = 2),
            ),
            sut.observeRelatedArtists("1").first()
        )
    }

    @Test
    fun testObserveRecentlyAddedSongs() = runTest {
        val now = System.currentTimeMillis().milliseconds.inWholeSeconds
        val atLeastTime = now - DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS.seconds.inWholeSeconds
        val atLeastTimePlusMinusSecond = atLeastTime + 1.seconds.inWholeSeconds

        mediaStoreGenreDao.replaceAll(
            genres = emptyList(),
            genresTracks = listOf(
                emptyMediaStoreGenreTrackEntity("genre 1", "1"),
                emptyMediaStoreGenreTrackEntity("genre 1", "2"),
                emptyMediaStoreGenreTrackEntity("genre 1", "3"),
                emptyMediaStoreGenreTrackEntity("genre 1", "4"),
                emptyMediaStoreGenreTrackEntity("genre 1", "5"),
                emptyMediaStoreGenreTrackEntity("genre 1", "6"),
                emptyMediaStoreGenreTrackEntity("genre 1", "7"),
                emptyMediaStoreGenreTrackEntity("genre 2", "20"),
            )
        )

        mediaStoreAudioDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", dateAdded = now, isPodcast = true), // should be skipped
            emptyMediaStoreAudioEntity(id = "2", dateAdded = now, isPodcast = true), // should be skipped
            // same date, order by title
            emptyMediaStoreAudioEntity(id = "3", dateAdded = now, title = "déh", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "4", dateAdded = now, title = "dEa", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "5", dateAdded = now, title = "dèb", isPodcast = false),
            // before permitted, should be skipped
            emptyMediaStoreAudioEntity(id = "6", dateAdded = atLeastTime, isPodcast = false),
            // one second after permitted
            emptyMediaStoreAudioEntity(id = "7", dateAdded = atLeastTimePlusMinusSecond, isPodcast = false),
        )

        val expected = listOf(
            Triple("4", now, "dEa"),
            Triple("5", now, "dèb"),
            Triple("3", now, "déh"),
            Triple("7", atLeastTimePlusMinusSecond, ""),
        )

        Assert.assertEquals(
            expected,
            sut.observeRecentlyAddedSongs("genre 1").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeRecentlyAddedSongs("missing").first(),
        )
    }

    private suspend fun setupSongsData() {
        val testData = TestData.items(false)
        val genreId = "1"
        mediaStoreAudioDao.replaceAll(testData)
        mediaStoreGenreDao.replaceAll(
            genres = listOf(emptyMediaStoreGenreEntity(genreId)),
            genresTracks = testData.map {
                emptyMediaStoreGenreTrackEntity(genreId, it.id)
            }
        )
    }

}