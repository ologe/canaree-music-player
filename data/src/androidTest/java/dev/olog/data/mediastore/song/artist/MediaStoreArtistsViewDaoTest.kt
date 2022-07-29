package dev.olog.data.mediastore.song.artist

import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.ArtistSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.MediaStoreTest
import dev.olog.data.TestData
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreArtistsViewDaoTest : MediaStoreTest(isPodcastTest = false) {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sortRepository = SortRepository(db.sortDao())
    private val sut = db.mediaStoreArtistDao()

    @Test
    fun testGetAll() = runTest {
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "1" to "<unknown>",
            "2" to "dEa artist 1",
            "4" to "dec artist 3",
            "3" to "déh artist 2",
        )
        val actual = sut.getAll().map { it.id to it.name }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByNameAsc() = runTest {
        sortRepository.setAllArtistsSort(AllArtistsSort(ArtistSortType.Name, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "2" to "dEa artist 1",
            "4" to "dec artist 3",
            "3" to "déh artist 2",
            "1" to "<unknown>",
            )
        val actual = sut.getAllSorted().map { it.id to it.name }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByNameDesc() = runTest {
        sortRepository.setAllArtistsSort(AllArtistsSort(ArtistSortType.Name, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            "3" to "déh artist 2",
            "4" to "dec artist 3",
            "2" to "dEa artist 1",
            "1" to "<unknown>",
        )
        val actual = sut.getAllSorted().map { it.id to it.name }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateAsc() = runTest {
        sortRepository.setAllArtistsSort(AllArtistsSort(ArtistSortType.Date, SortDirection.ASCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("1", -10001L, "<unknown>"),
            Triple("2", 201L, "dEa artist 1"),
            Triple("4", 201L, "dec artist 3"),
            Triple("3", 301L, "déh artist 2"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.name) }

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetAllSortedByDateDesc() = runTest {
        sortRepository.setAllArtistsSort(AllArtistsSort(ArtistSortType.Date, SortDirection.DESCENDING))
        mediaStoreDao.insertAll(TestData.items(false))

        val expected = listOf(
            Triple("3", 301L, "déh artist 2"),
            Triple("4", 201L, "dec artist 3"),
            Triple("2", 201L, "dEa artist 1"),
            Triple("1", -10001L, "<unknown>"),
        )
        val actual = sut.getAllSorted().map { Triple(it.id, it.dateAdded, it.name) }

        Assert.assertEquals(expected, actual)
    }

}