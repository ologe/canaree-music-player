package dev.olog.data.mediastore.song.folder

import dev.olog.core.entity.sort.AllFoldersSort
import dev.olog.core.entity.sort.FolderSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.MediaStoreTest
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreFoldersView
import dev.olog.data.emptyMediaStoreFoldersViewSorted
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MediaStoreFoldersViewDaoTest : MediaStoreTest(isPodcastTest = false) {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sortRepository = SortRepository(db.sortDao())
    private val sut = db.mediaStoreFoldersDao()

    @Test
    fun testGetAll() = runTest {
        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 150),
            emptyMediaStoreAudioEntity(id = "2", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 100),
            emptyMediaStoreAudioEntity(id = "3", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 200),
            emptyMediaStoreAudioEntity(id = "4", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 500),
            emptyMediaStoreAudioEntity(id = "5", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 300),
            emptyMediaStoreAudioEntity(id = "6", directory = "storage/b/dEa", directoryName = "dEa", isPodcast = false, dateAdded = 1000),
        )

        val expected = listOf(
            "storage/a/dèb",
            "storage/b/dEa",
            "storage/déh",
        )

        Assert.assertEquals(expected, sut.getAll().map { it.path })
    }

    @Test
    fun testGetAllSortedByNameAsc() = runTest {
        sortRepository.setAllFolderSort(AllFoldersSort(FolderSortType.Title, SortDirection.ASCENDING))

        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 150),
            emptyMediaStoreAudioEntity(id = "2", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 100),
            emptyMediaStoreAudioEntity(id = "3", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 200),
            emptyMediaStoreAudioEntity(id = "4", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 500),
            emptyMediaStoreAudioEntity(id = "5", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 300),
            emptyMediaStoreAudioEntity(id = "6", directory = "storage/b/dEa", directoryName = "dEa", isPodcast = false, dateAdded = 1000),
        )

        val expected = listOf(
            emptyMediaStoreFoldersViewSorted(path = "storage/b/dEa", name = "dEa", songs = 1, dateAdded = 1000),
            emptyMediaStoreFoldersViewSorted(path = "storage/a/dèb", name = "dèb", songs = 2, dateAdded = 300),
            emptyMediaStoreFoldersViewSorted(path = "storage/déh", name = "déh", songs = 3, dateAdded = 100),
        )

        Assert.assertEquals(expected, sut.getAllSorted())
    }

    @Test
    fun testGetAllSortedByNameDesc() = runTest {
        sortRepository.setAllFolderSort(AllFoldersSort(FolderSortType.Title, SortDirection.DESCENDING))

        mediaStoreDao.insertAll(
            emptyMediaStoreAudioEntity(id = "1", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 150),
            emptyMediaStoreAudioEntity(id = "2", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 100),
            emptyMediaStoreAudioEntity(id = "3", directory = "storage/déh", directoryName = "déh", isPodcast = false, dateAdded = 200),
            emptyMediaStoreAudioEntity(id = "4", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 500),
            emptyMediaStoreAudioEntity(id = "5", directory = "storage/a/dèb", directoryName = "dèb", isPodcast = false, dateAdded = 300),
            emptyMediaStoreAudioEntity(id = "6", directory = "storage/b/dEa", directoryName = "dEa", isPodcast = false, dateAdded = 1000),
        )

        val expected = listOf(
            emptyMediaStoreFoldersViewSorted(path = "storage/déh", name = "déh", songs = 3, dateAdded = 100),
            emptyMediaStoreFoldersViewSorted(path = "storage/a/dèb", name = "dèb", songs = 2, dateAdded = 300),
            emptyMediaStoreFoldersViewSorted(path = "storage/b/dEa", name = "dEa", songs = 1, dateAdded = 1000),
        )

        Assert.assertEquals(expected, sut.getAllSorted())
    }

}