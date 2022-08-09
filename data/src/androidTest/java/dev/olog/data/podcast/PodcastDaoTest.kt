package dev.olog.data.podcast

import dev.olog.core.entity.sort.AllPodcastsSort
import dev.olog.core.entity.sort.PodcastSortType
import dev.olog.core.entity.sort.SortDirection
import dev.olog.data.DatabaseTest
import dev.olog.data.emptyMediaStoreAudioEntity
import dev.olog.data.emptyMediaStorePodcastSortedView
import dev.olog.data.emptyMediaStorePodcastView
import dev.olog.data.sort.SortRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PodcastDaoTest : DatabaseTest() {

    private val mediaStoreDao = db.mediaStoreAudioDao()
    private val sut = db.podcastDao()

    @Before
    fun setup() = runTest {
        // actual db view behaviour is tested in MediaStorePodcastViewDaoTest
        mediaStoreDao.insertAll(
            listOf(
                emptyMediaStoreAudioEntity(id = "id 1", title = "title 1", isPodcast = true),
                emptyMediaStoreAudioEntity(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
                emptyMediaStoreAudioEntity(id = "id 3", title = "title 3", displayName = "displayName", isPodcast = true),
            )
        )
    }

    @Test
    fun testGetAll() {
        val sort = AllPodcastsSort(PodcastSortType.Title, SortDirection.ASCENDING)
        SortRepository(db.sortDao()).setAllPodcastsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastSortedView(id = "id 1", title = "title 1", isPodcast = true),
            emptyMediaStorePodcastSortedView(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
            emptyMediaStorePodcastSortedView(id = "id 3", title = "title 3", displayName = "displayName", isPodcast = true),
        )
        val actual = sut.getAll()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testObserveAll() = runTest {
        val sort = AllPodcastsSort(PodcastSortType.Title, SortDirection.ASCENDING)
        SortRepository(db.sortDao()).setAllPodcastsSort(sort)

        val expected = listOf(
            emptyMediaStorePodcastSortedView(id = "id 1", title = "title 1", isPodcast = true),
            emptyMediaStorePodcastSortedView(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
            emptyMediaStorePodcastSortedView(id = "id 3", title = "title 3", displayName = "displayName", isPodcast = true),
        )
        val actual = sut.observeAll().first()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testGetById() {
        Assert.assertEquals(
            emptyMediaStorePodcastView(id = "id 1", title = "title 1", isPodcast = true),
            sut.getById("id 1"),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastView(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
            sut.getById("id 2"),
        )
        Assert.assertEquals(
            null,
            sut.getById("id 10"),
        )
    }

    @Test
    fun testObserveById() = runTest {
        Assert.assertEquals(
            emptyMediaStorePodcastView(id = "id 1", title = "title 1", isPodcast = true),
            sut.observeById("id 1").first(),
        )
        Assert.assertEquals(
            emptyMediaStorePodcastView(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
            sut.observeById("id 2").first(),
        )
        Assert.assertEquals(
            null,
            sut.observeById("id 10").first(),
        )
    }

    @Test
    fun testGetByDisplayName() {
        Assert.assertEquals(
            emptyMediaStorePodcastView(id = "id 3", title = "title 3", displayName = "displayName", isPodcast = true),
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
            emptyMediaStorePodcastView(id = "id 2", title = "title 2", albumId = "albumId", isPodcast = true),
            sut.getByAlbumId("albumId"),
        )
        Assert.assertEquals(
            null,
            sut.getByAlbumId("missing"),
        )
    }

}