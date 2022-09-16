package dev.olog.data.song.folder

import dev.olog.core.Quadruple
import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.FolderSongsSort
import dev.olog.core.entity.sort.FolderSongsSortType
import dev.olog.core.entity.sort.FolderSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DataConstants
import dev.olog.data.DatabaseTest
import dev.olog.data.TestData
import dev.olog.data.blacklist.db.BlacklistEntity
import dev.olog.data.emptyMediaStoreArtistView
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreFoldersView
import dev.olog.data.emptyMediaStoreFoldersViewSorted
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

class FolderDaoTest : DatabaseTest() {

    private val sortRepository = SortRepository(db.sortDao())
    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.folderDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStoreFoldersViewDaoTest
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 2", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 3", directory = "storage/dir 2", directoryName = "dir 2", isPodcast = false),
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllFoldersSort(FolderSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllFolderSort(sort)

        val expected = listOf(
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 1", name = "dir 1", songs = 2),
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 2", name = "dir 2", songs = 1),
        )

        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllFoldersSort(FolderSortType.Title, SortDirection.ASCENDING)
        sortRepository.setAllFolderSort(sort)

        val expected = listOf(
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 1", name = "dir 1", songs = 2),
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 2", name = "dir 2", songs = 1),
        )

        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetByDirectory() = runTest {
        Assert.assertEquals(
            emptyMediaStoreFoldersView(path = "storage/dir 1", name = "dir 1", songs = 2),
            sut.getByDirectory("storage/dir 1"),
        )
        Assert.assertEquals(
            emptyMediaStoreFoldersView(path = "storage/dir 2", name = "dir 2", songs = 1),
            sut.getByDirectory("storage/dir 2"),
        )
        Assert.assertEquals(
            null,
            sut.getByDirectory("storage/dir 3"),
        )
    }


    @Test
    fun testObserveByDirectory() = runTest {
        Assert.assertEquals(
            emptyMediaStoreFoldersView(path = "storage/dir 1", name = "dir 1", songs = 2),
            sut.observeByDirectory("storage/dir 1").first(),
        )
        Assert.assertEquals(
            emptyMediaStoreFoldersView(path = "storage/dir 2", name = "dir 2", songs = 1),
            sut.observeByDirectory("storage/dir 2").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeByDirectory("storage/dir 3").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByTitleAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Title, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { it.id to it.title },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { it.id to it.title },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByTitleDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Title, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { it.id to it.title },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { it.id to it.title },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByArtistAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Artist, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByArtistDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Artist, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.artist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByAlbumAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Album, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.album, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByAlbumDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Album, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.album, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.album, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByAlbumArtistAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.AlbumArtist, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.albumArtist, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByAlbumArtistDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.AlbumArtist, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.albumArtist, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.albumArtist, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByDurationAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Duration, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByDurationDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Duration, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.duration, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.duration, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByDateAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByDateDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

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
            sut.getTracksByDirectory("storage/directory").map { Triple(it.id, it.dateAdded, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing").map { it.id to it.title },
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first().map { it.id to it.title },
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByTrackNumberAsc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.TrackNumber, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Quadruple("100", 0, 0, "aaa track"),
            Quadruple("300", 0, 0, "dEG"),
            Quadruple("101", 0, 0, "zzz track"),
            Quadruple("200", 1, 1, "dec"),
            Quadruple("400", 1, 1, "hello"),
            Quadruple("201", 1, 2, "dèb"),
            Quadruple("210", 2, 1, "déh"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("212", 2, 3, "ggg"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksByDirectory("storage/directory").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testgetTracksByDirectorySortedByTrackNumberDesc() = runTest {
        sortRepository.setFolderSongsSort(FolderSongsSort(FolderSongsSortType.TrackNumber, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Quadruple("212", 2, 3, "ggg"),
            Quadruple("211", 2, 2, "dEa"),
            Quadruple("210", 2, 1, "déh"),
            Quadruple("201", 1, 2, "dèb"),
            Quadruple("400", 1, 1, "hello"),
            Quadruple("200", 1, 1, "dec"),
            Quadruple("101", 0, 0, "zzz track"),
            Quadruple("300", 0, 0, "dEG"),
            Quadruple("100", 0, 0, "aaa track"),
        )

        Assert.assertEquals(
            expected,
            sut.getTracksByDirectory("storage/directory").map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )
        Assert.assertEquals(
            expected,
            sut.observeTracksByDirectory("storage/directory").first().map { Quadruple(it.id, it.discNumber, it.trackNumber, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.getTracksByDirectory("missing"),
        )
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeTracksByDirectory("missing").first(),
        )
    }

    @Test
    fun testGetAllBlacklistedIncluded() = runTest {
        val blacklistDao = db.blacklistDao()
        blacklistDao.insertAll(BlacklistEntity("storage/dir 1"))
        blacklistDao.insertAll(BlacklistEntity("storage/dir 3"))

        val expected = listOf(
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 1", name = "dir 1", songs = 2),
            emptyMediaStoreFoldersViewSorted(path = "storage/dir 2", name = "dir 2", songs = 1),
        )

        val actual = sut.getAllBlacklistedIncluded()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testMostPlayed() = runTest {
        // initial
        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeMostPlayed("storage/dir 1").first()
        )

        // add one less than needed
        (0 until DataConstants.MIN_MOST_PLAYED_TIMES - 1).forEach {
            sut.insertMostPlayed("storage/dir 1", "id 1")
        }

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeMostPlayed("storage/dir 1").first()
        )

        // add enough for min
        sut.insertMostPlayed("storage/dir 1", "id 1")
        Assert.assertEquals(
            listOf(emptyMediaStoreSongView(id = "id 1", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),),
            sut.observeMostPlayed("storage/dir 1").first()
        )

        // add another more
        (0 until DataConstants.MIN_MOST_PLAYED_TIMES * 2).forEach {
            sut.insertMostPlayed("storage/dir 1", "id 2")
        }

        Assert.assertEquals(
            listOf(
                emptyMediaStoreSongView(id = "id 2", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
                emptyMediaStoreSongView(id = "id 1", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
            ),
            sut.observeMostPlayed("storage/dir 1").first()
        )
    }

    @Test
    fun testObserveSiblings() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 2", directory = "storage/dir 1", directoryName = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 3", directory = "storage/dir 2", directoryName = "dir 2", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 4", directory = "storage/dir 3", directoryName = "dir 3", isPodcast = false),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "storage/dir 1", name = "dir 1", songs = 2),
                emptyMediaStoreFoldersView(path = "storage/dir 3", name = "dir 3", songs = 1),
            ),
            sut.observeSiblings("storage/dir 2").first()
        )
    }

    @Test
    fun testObserveRelatedArtists() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "id 1", artistId = "artistId 10", artist = "déh", directory = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 2", artistId = "artistId 10", artist = "déh", directory = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 3", artistId = "artistId 20", artist = "dEa", directory = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 4", artistId = "artistId 30", artist = "dèb", directory = "dir 1", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "id 5", artistId = "artistId 40", artist = "artist 4", directory = "dir 2", isPodcast = false),
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreArtistView(id = "artistId 20", name = "dEa", songs = 1),
                emptyMediaStoreArtistView(id = "artistId 30", name = "dèb", songs = 1),
                emptyMediaStoreArtistView(id = "artistId 10", name = "déh", songs = 2),
            ),
            sut.observeRelatedArtists("dir 1").first()
        )
    }

    @Test
    fun testObserveRecentlyAddedSongs() = runTest {
        val now = System.currentTimeMillis().milliseconds.inWholeSeconds
        val atLeastTime = now - DataConstants.RECENTLY_ADDED_PERIOD_IN_SECONDS.seconds.inWholeSeconds
        val atLeastTimePlusMinusSecond = atLeastTime + 1.seconds.inWholeSeconds

        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", dateAdded = now, directory = "dir", isPodcast = true), // should be skipped
            emptyMediaStoreAudioEntity(id = "2", dateAdded = now, directory = "another dir", isPodcast = true), // should be skipped
            // same date, order by title
            emptyMediaStoreAudioEntity(id = "3", dateAdded = now, title = "déh", directory = "dir", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "4", dateAdded = now, title = "dEa", directory = "dir", isPodcast = false),
            emptyMediaStoreAudioEntity(id = "5", dateAdded = now, title = "dèb", directory = "dir", isPodcast = false),
            // before permitted, should be skipped
            emptyMediaStoreAudioEntity(id = "6", dateAdded = atLeastTime, directory = "dir", isPodcast = false),
            // one second after permitted
            emptyMediaStoreAudioEntity(id = "7", dateAdded = atLeastTimePlusMinusSecond, directory = "dir", isPodcast = false),
        )

        val expected = listOf(
            Triple("4", now, "dEa"),
            Triple("5", now, "dèb"),
            Triple("3", now, "déh"),
            Triple("7", atLeastTimePlusMinusSecond, ""),
        )

        Assert.assertEquals(
            expected,
            sut.observeRecentlyAddedSongs("dir").first().map { Triple(it.id, it.dateAdded, it.title) },
        )

        Assert.assertEquals(
            emptyList<MediaStoreSongsView>(),
            sut.observeRecentlyAddedSongs("missing dir").first(),
        )
    }

    @Test
    fun testGetDirectorySubFolders() = runTest {
        mediaStoreDao.replaceAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "1", directory = "/storage", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "2", directory = "/storage/dir 1", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "3", directory = "/storage/dir 2", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "4", directory = "/storage/dir 1/abc", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "5", directory = "/storage/dir 1/def", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "6", directory = "/storage 2", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "7", directory = "/storage 2/dir 10", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "8", directory = "/storage 2/dir 20", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "9", directory = "/storage 2/dir 10/aaa", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "10", directory = "/storage 2/dir 20/bbb", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "11", directory = "/storage 2/dir 20/zzz", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "12", directory = "/storage 2/dir 20/zzz", isPodcast = false),
            )
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "/storage/dir 1", songs = 1),
                emptyMediaStoreFoldersView(path = "/storage/dir 2", songs = 1),
            ),
            sut.getDirectorySubFolders("/storage")
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "/storage/dir 1/abc", songs = 1),
                emptyMediaStoreFoldersView(path = "/storage/dir 1/def", songs = 1),
            ),
            sut.getDirectorySubFolders("/storage/dir 1")
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "/storage 2/dir 10", songs = 1),
                emptyMediaStoreFoldersView(path = "/storage 2/dir 20", songs = 1),
            ),
            sut.getDirectorySubFolders("/storage 2")
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "/storage 2/dir 10/aaa", songs = 1),
            ),
            sut.getDirectorySubFolders("/storage 2/dir 10")
        )

        Assert.assertEquals(
            listOf(
                emptyMediaStoreFoldersView(path = "/storage 2/dir 20/bbb", songs = 1),
                emptyMediaStoreFoldersView(path = "/storage 2/dir 20/zzz", songs = 2),
            ),
            sut.getDirectorySubFolders("/storage 2/dir 20")
        )
    }

}