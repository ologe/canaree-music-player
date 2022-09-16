package dev.olog.data.mediastore.song.album

import dev.olog.core.entity.sort.AlbumSortType
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.MediaStoreTest
import dev.olog.data.TestData
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreAlbumsViewDaoTest : MediaStoreTest(isPodcastTest = false) {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sortRepository = SortRepository(db.sortDao())
    private val sut = db.mediaStoreAlbumsViewDao()

    @Test
    fun testGetAll() = runTest {
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "10",
            "20",
            "21",
            "30",
            "40",
        )
        val actual = sut.getAll().map { it.id }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByTitleAsc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Title, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "30" to "dEa another album",
            "21" to "dec album 2",
            "40" to "dEg artist 3 album",
            "20" to "déh album 1",
            "10" to "<unknown>",
            )
        val actual = sut.getAllSorted().map { it.id to it.title }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByTitleDesc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Title, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "20" to "déh album 1",
            "40" to "dEg artist 3 album",
            "21" to "dec album 2",
            "30" to "dEa another album",
            "10" to "<unknown>",
        )
        val actual = sut.getAllSorted().map { it.id to it.title }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByArtistAsc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Artist, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("21", "dEa artist 1", "dec album 2"),
            Triple("20", "dEa artist 1", "déh album 1"),
            Triple("40", "dec artist 3", "dEg artist 3 album"),
            Triple("30", "déh artist 2", "dEa another album"),
            Triple("10", "<unknown>", "<unknown>"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.artist, it.title) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByArtistDesc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Artist, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("30", "déh artist 2", "dEa another album"),
            Triple("40", "dec artist 3", "dEg artist 3 album"),
            Triple("20", "dEa artist 1", "déh album 1"),
            Triple("21", "dEa artist 1", "dec album 2"),
            Triple("10", "<unknown>", "<unknown>"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.artist, it.title) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateAsc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("10", -10001L, "<unknown>"),
            Triple("21", 201L, "dec album 2"),
            Triple("40", 201L, "dEg artist 3 album"),
            Triple("20", 201L, "déh album 1"),
            Triple("30", 301L, "dEa another album"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.title) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateDesc() = runTest {
        sortRepository.setAllAlbumsSort(AllAlbumsSort(AlbumSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("30", 301L, "dEa another album"),
            Triple("20", 201L, "déh album 1"),
            Triple("40", 201L, "dEg artist 3 album"),
            Triple("21", 201L, "dec album 2"),
            Triple("10", -10001L, "<unknown>"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.title) }

        Assert.assertEquals(expected, actual)
    }

}