package dev.olog.data.song

import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.SongSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DatabaseTest
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStoreSongView
import dev.olog.data.emptyMediaStoreSortedSongView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SongDaoTest : DatabaseTest() {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.songDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStoreSongViewDaoTest
        mediaStoreDao.insertAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "1", title = "title 1", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
                emptyMediaStoreAudioEntity(id = "3", title = "title 3", displayName = "displayName", isPodcast = false),
            )
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllSongsSort(SongSortType.Title, SortDirection.ASCENDING)
        SortRepository(db.sortDao()).setAllSongsSort(sort)

        val expected = listOf(
            emptyMediaStoreSortedSongView(id = "1", title = "title 1", isPodcast = false),
            emptyMediaStoreSortedSongView(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
            emptyMediaStoreSortedSongView(id = "3", title = "title 3", displayName = "displayName", isPodcast = false),
        )
        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllSongsSort(SongSortType.Title, SortDirection.ASCENDING)
        SortRepository(db.sortDao()).setAllSongsSort(sort)

        val expected = listOf(
            emptyMediaStoreSortedSongView(id = "1", title = "title 1", isPodcast = false),
            emptyMediaStoreSortedSongView(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
            emptyMediaStoreSortedSongView(id = "3", title = "title 3", displayName = "displayName", isPodcast = false),
        )
        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() {
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "1", title = "title 1", isPodcast = false),
            sut.getById("1"),
        )
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
            sut.getById("2"),
        )
        Assert.assertEquals(
            null,
            sut.getById("10"),
        )
    }

    @Test
    fun testObserveById() = runTest {
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "1", title = "title 1", isPodcast = false),
            sut.observeById("1").first(),
        )
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
            sut.observeById("2").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("10").first(),
        )
    }

    @Test
    fun testGetByDisplayName() {
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "3", title = "title 3", displayName = "displayName", isPodcast = false),
            sut.getByDisplayName("displayName"),
        )
        Assert.assertEquals(
            null,
            sut.getByDisplayName("missing"),
        )
    }

    @Test
    fun testGetByAlbumId() {
        Assert.assertEquals(
            emptyMediaStoreSongView(id = "2", title = "title 2", albumId = "albumId", isPodcast = false),
            sut.getByAlbumId("albumId"),
        )
        Assert.assertEquals(
            null,
            sut.getByAlbumId("missing"),
        )
    }

}