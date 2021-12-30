package dev.olog.data.playable

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaStoreSong
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.core.sort.TrackSort
import dev.olog.data.index.IndexedPlayablesQueries
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.net.URI

class TrackRepositoryTest {

    private val repo = mock<MediaStoreSongRepository>()
    private val podcastRepo = mock<MediaStorePodcastEpisodeRepository>()
    private val operations = mock<PlayableMediaStoreOperations>()
    private val queries = mock<IndexedPlayablesQueries>()
    private val sut = TrackRepository(
        schedulers = TestSchedulers(),
        repository = repo,
        podcastRepository = podcastRepo,
        operations = operations,
        indexedPlayablesQueries = queries,
    )

    companion object {
        private val list = listOf(
            MediaStoreSong("1"),
            MediaStoreSong("2"),
        )
        private val item = MediaStoreSong("3")
        private val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "10",
            isPodcast = false
        )
        private val podcastUri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Track,
            id = "11",
            isPodcast = true
        )

        private val sort = Sort(TrackSort.Title, SortDirection.ASCENDING)
    }

    @Test
    fun `test getAll song`() {
        whenever(repo.getAll()).thenReturn(list)
        val actual = sut.getAll(MediaStoreType.Song)
        Assert.assertEquals(list, actual)
    }

    @Test
    fun `test getAll podcast`() {
        whenever(podcastRepo.getAll()).thenReturn(list)
        val actual = sut.getAll(MediaStoreType.Podcast)
        Assert.assertEquals(list, actual)
    }

    @Test
    fun `test observeAll song`() = runTest {
        whenever(repo.observeAll()).thenReturn(flowOf(list))
        sut.observeAll(MediaStoreType.Song).test(this) {
            assertValue(list)
        }
    }

    @Test
    fun `test observeAll podcast`() = runTest {
        whenever(podcastRepo.observeAll()).thenReturn(flowOf(list))
        sut.observeAll(MediaStoreType.Podcast).test(this) {
            assertValue(list)
        }
    }

    @Test
    fun `test getById song`() {
        whenever(repo.getById(uri.id)).thenReturn(item)
        val actual = sut.getById(uri)
        Assert.assertEquals(item, actual)
    }

    @Test
    fun `test getById podcast`() {
        whenever(podcastRepo.getById(podcastUri.id)).thenReturn(item)
        val actual = sut.getById(podcastUri)
        Assert.assertEquals(item, actual)
    }

    @Test
    fun `test getByCollectionId song`() {
        whenever(repo.getByCollectionId(uri.id)).thenReturn(list)
        val actual = sut.getByCollectionId(uri)
        Assert.assertEquals(list, actual)
    }

    @Test
    fun `test getByCollectionId podcast`() {
        whenever(podcastRepo.getByCollectionId(podcastUri.id)).thenReturn(list)
        val actual = sut.getByCollectionId(podcastUri)
        Assert.assertEquals(list, actual)
    }

    @Test
    fun `test getByUri song`() {
        val javaUri = URI("canaree:track:id")
        whenever(operations.getByUri(javaUri)).thenReturn(uri)
        whenever(repo.getById(uri.id)).thenReturn(item)

        val actual = sut.getByUri(javaUri)
        Assert.assertEquals(item, actual)
    }

    @Test
    fun `test getByUri podcast`() {
        val javaUri = URI("canaree:track:id")
        whenever(operations.getByUri(javaUri)).thenReturn(podcastUri)
        whenever(podcastRepo.getById(podcastUri.id)).thenReturn(item)

        val actual = sut.getByUri(javaUri)
        Assert.assertEquals(item, actual)
    }

    @Test
    fun `test getByUri, uri not found should return null`() {
        val javaUri = URI("canaree:track:id")
        whenever(operations.getByUri(javaUri)).thenReturn(null)

        val actual = sut.getByUri(javaUri)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeById song`() = runTest {
        whenever(repo.observeById(uri.id)).thenReturn(flowOf(item))
        sut.observeById(uri).test(this) {
            assertValue(item)
        }
    }

    @Test
    fun `test observeById podcast`() = runTest {
        whenever(podcastRepo.observeById(podcastUri.id)).thenReturn(flowOf(item))
        sut.observeById(podcastUri).test(this) {
            assertValue(item)
        }
    }

    @Test
    fun `test getSort song`() {
        whenever(repo.getSort()).thenReturn(sort)
        val actual = sut.getSort(MediaStoreType.Song)
        Assert.assertEquals(sort, actual)
    }

    @Test
    fun `test getSort podcast`() {
        whenever(podcastRepo.getSort()).thenReturn(sort)
        val actual = sut.getSort(MediaStoreType.Podcast)
        Assert.assertEquals(sort, actual)
    }

    @Test
    fun `test setSort song`() = runTest {
        sut.setSort(MediaStoreType.Song, sort)
        verify(repo).setSort(sort)
    }

    @Test
    fun `test setSort podcast`() = runTest {
        sut.setSort(MediaStoreType.Podcast, sort)
        verify(podcastRepo).setSort(sort)
    }

    @Test
    fun `test delete`() = runTest {
        val uris = listOf(uri, podcastUri)

        // song
        whenever(repo.getById(uri.id)).thenReturn(MediaStoreSong(uri.id))
        whenever(operations.delete(MediaStoreSong(uri.id))).thenReturn(uri)

        // podcast
        whenever(podcastRepo.getById(podcastUri.id)).thenReturn(MediaStoreSong(podcastUri.id))
        whenever(operations.delete(MediaStoreSong(podcastUri.id))).thenReturn(podcastUri)

        // can't find track
        whenever(podcastRepo.getById("3")).thenReturn(null)
        whenever(operations.delete(null)).thenReturn(null)

        // can't delete
        whenever(podcastRepo.getById("4")).thenReturn(MediaStoreSong("4"))
        whenever(operations.delete(MediaStoreSong("4"))).thenReturn(null)

        sut.delete(uris)

        verify(queries).delete(uri.id)
        verify(queries).delete(podcastUri.id)
    }

    @Test(expected = IllegalStateException::class)
    fun `test getPodcastCurrentPosition song, should throw`() {
        sut.getPodcastCurrentPosition(uri, 100)
    }

    @Test
    fun `test getPodcastCurrentPosition podcast`() {
        whenever(podcastRepo.getCurrentPosition(podcastUri.id, 100)).thenReturn(10)
        val actual = sut.getPodcastCurrentPosition(podcastUri, 100)
        Assert.assertEquals(10, actual)
    }

    @Test(expected = IllegalStateException::class)
    fun `test savePodcastCurrentPosition song, should throw`() {
        sut.savePodcastCurrentPosition(uri, 100)
    }

    @Test
    fun `test savePodcastCurrentPosition`() {
        // don't care if podcast or not
        whenever(podcastRepo.getCurrentPosition(podcastUri.id, 100)).thenReturn(10)
        val actual = sut.getPodcastCurrentPosition(podcastUri, 100)
        Assert.assertEquals(10, actual)
    }

}