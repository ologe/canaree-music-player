package dev.olog.data.mediastore.song

import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.SongSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.MediaStoreTest
import dev.olog.data.TestData
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreSongsViewDaoTest : MediaStoreTest(isPodcastTest = false) {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sortRepository = SortRepository(db.sortDao())
    private val sut = db.mediaStoreSongDao()

    @Test
    fun testGetAll() = runTest {
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

        val actual = sut.getAll().map { it.id to it.title }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByTitleAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Title, SortDirection.ASCENDING))
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

        val actual = sut.getAllSorted().map { it.id to it.title }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByTitleDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Title, SortDirection.DESCENDING))
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

        val actual = sut.getAllSorted().map { it.id to it.title }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByArtistAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Artist, SortDirection.ASCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.artist, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByArtistDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Artist, SortDirection.DESCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.artist, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByAlbumAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Album, SortDirection.ASCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.album, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByAlbumDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Album, SortDirection.DESCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.album, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDurationAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Duration, SortDirection.ASCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.duration, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDurationDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Duration, SortDirection.DESCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.duration, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateAsc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Date, SortDirection.ASCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.title) }
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateDesc() = runTest {
        sortRepository.setAllSongsSort(AllSongsSort(SongSortType.Date, SortDirection.DESCENDING))
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

        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.title) }
        Assert.assertEquals(expected, actual)
    }

}