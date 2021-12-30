package dev.olog.data.author

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaStoreArtist
import dev.olog.core.MediaStoreSong
import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.sort.AuthorDetailSort
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class AuthorRepositoryTest {

    private val repo = mock<MediaStoreArtistRepository>()
    private val podcastRepo = mock<MediaStorePodcastAuthorRepository>()
    private val sut = AuthorRepository(
        schedulers = TestSchedulers(),
        repository = repo,
        podcastRepository = podcastRepo,
    )

    companion object {
        private val list = listOf(
            MediaStoreArtist("1"),
            MediaStoreArtist("2"),
        )
        private val item = MediaStoreArtist("3")
        private val uri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Author,
            id = "10",
            isPodcast = false
        )
        private val podcastUri = MediaUri(
            source = MediaUri.Source.MediaStore,
            category = MediaUri.Category.Author,
            id = "11",
            isPodcast = true
        )
        private val tracks = listOf(
            MediaStoreSong("1"),
            MediaStoreSong("2"),
        )
        private val sort = Sort(
            AuthorSort.Name,
            SortDirection.ASCENDING,
        )
        private val detailSort = Sort(
            AuthorDetailSort.Title,
            SortDirection.ASCENDING,
        )
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
    fun `test getTracksById song`() {
        whenever(repo.getTracksById(uri.id)).thenReturn(tracks)
        val actual = sut.getTracksById(uri)
        Assert.assertEquals(tracks, actual)
    }

    @Test
    fun `test getTracksById podcast`() {
        whenever(podcastRepo.getTracksById(podcastUri.id)).thenReturn(tracks)
        val actual = sut.getTracksById(podcastUri)
        Assert.assertEquals(tracks, actual)
    }

    @Test
    fun `test observeTracksById song`() = runTest {
        whenever(repo.observeTracksById(uri.id)).thenReturn(flowOf(tracks))
        sut.observeTracksById(uri).test(this) {
            assertValue(tracks)
        }
    }

    @Test
    fun `test observeTracksById podcast`() = runTest {
        whenever(podcastRepo.observeTracksById(podcastUri.id)).thenReturn(flowOf(tracks))
        sut.observeTracksById(podcastUri).test(this) {
            assertValue(tracks)
        }
    }

    @Test
    fun `test observeRecentlyPlayed song`() = runTest {
        whenever(repo.observeRecentlyPlayed()).thenReturn(flowOf(list))
        sut.observeRecentlyPlayed(MediaStoreType.Song).test(this) {
            assertValue(list)
        }
    }

    @Test
    fun `test observeRecentlyPlayed podcast`() = runTest {
        whenever(podcastRepo.observeRecentlyPlayed()).thenReturn(flowOf(list))
        sut.observeRecentlyPlayed(MediaStoreType.Podcast).test(this) {
            assertValue(list)
        }
    }

    @Test
    fun `test addToRecentlyPlayed song`() = runTest {
        sut.addToRecentlyPlayed(uri)
        verify(repo).addToRecentlyPlayed(uri.id)
    }

    @Test
    fun `test addToRecentlyPlayed podcast`() = runTest {
        sut.addToRecentlyPlayed(podcastUri)
        verify(podcastRepo).addToRecentlyPlayed(podcastUri.id)
    }

    @Test
    fun `test observeRecentlyAdded song`() = runTest {
        whenever(repo.observeRecentlyAdded()).thenReturn(flowOf(list))
        sut.observeRecentlyAdded(MediaStoreType.Song).test(this) {
            assertValue(list)
        }
    }

    @Test
    fun `test observeRecentlyAdded podcast`() = runTest {
        whenever(podcastRepo.observeRecentlyAdded()).thenReturn(flowOf(list))
        sut.observeRecentlyAdded(MediaStoreType.Podcast).test(this) {
            assertValue(list)
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
    fun `test getDetailSort song`() {
        whenever(repo.getDetailSort()).thenReturn(detailSort)
        val actual = sut.getDetailSort(MediaStoreType.Song)
        Assert.assertEquals(detailSort, actual)
    }

    @Test
    fun `test getDetailSort podcast`() {
        whenever(podcastRepo.getDetailSort()).thenReturn(detailSort)
        val actual = sut.getDetailSort(MediaStoreType.Podcast)
        Assert.assertEquals(detailSort, actual)
    }

    @Test
    fun `test observeDetailSort song`() = runTest {
        whenever(repo.observeDetailSort()).thenReturn(flowOf(detailSort))
        sut.observeDetailSort(MediaStoreType.Song).test(this) {
            assertValue(detailSort)
        }
    }

    @Test
    fun `test observeDetailSort podcast`() = runTest {
        whenever(podcastRepo.observeDetailSort()).thenReturn(flowOf(detailSort))
        sut.observeDetailSort(MediaStoreType.Podcast).test(this) {
            assertValue(detailSort)
        }
    }

    @Test
    fun `test setDetailSort song`() = runTest {
        sut.setDetailSort(MediaStoreType.Song, detailSort)
        verify(repo).setDetailSort(detailSort)
    }

    @Test
    fun `test setDetailSort podcast`() = runTest {
        sut.setDetailSort(MediaStoreType.Podcast, detailSort)
        verify(podcastRepo).setDetailSort(detailSort)
    }

}