package dev.olog.data.collection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaStoreAlbum
import dev.olog.core.MediaStoreSong
import dev.olog.core.sort.CollectionDetailSort
import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.AlbumView
import dev.olog.testing.IndexedTrack
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStoreAlbumRepositoryTest {

    private val queries = mock<AlbumsQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = MediaStoreAlbumRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        sortDao = sortDao,
        dateTimeFactory = dateTimeFactory,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll`() {
        val query = QueryList(AlbumView(id = "1", songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(AlbumView(id = "1", songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(AlbumView(id = "1", songs = 2))
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = sut.getById("1")
        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Albums_view>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = sut.getById("1")
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(AlbumView(id = "1", songs = 2))
        whenever(queries.selectById("1")).thenReturn(query)

        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2,
        )

        sut.observeById("1").test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Albums_view>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        sut.observeById("1").test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedTrack(id = "1", is_podcast = false))
        whenever(queries.selectTracksByIdSorted("1")).thenReturn(query)

        val actual = sut.getPlayablesById("1")
        val expected = MediaStoreSong(id = "1")

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedTrack(id = "1", is_podcast = false))
        whenever(queries.selectTracksByIdSorted("1")).thenReturn(query)

        val expected = MediaStoreSong(id = "1")

        sut.observePlayablesById("1").test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(AlbumView(id = "1", songs = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2,
        )

        sut.observeRecentlyPlayed().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test addRecentlyPlayed`() = runTest {
        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)

        sut.addToRecentlyPlayed("10")

        verify(queries).insertRecentlyPlayed("10", 100)
    }

    @Test
    fun `test observeRecentlyAdded`() = runTest {
        val query = QueryList(AlbumView(id = "1", songs = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2,
        )

        sut.observeRecentlyAdded().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeSiblings`() = runTest {
        val query = QueryOneOrNull(AlbumView(id = "1", author_id = "2", songs = 3))
        whenever(queries.selectById("1")).thenReturn(query)

        val artistAlbumsQuery = QueryList(
            AlbumView(id = "1", songs = 2),
            AlbumView(id = "2", songs = 4),
            AlbumView(id = "3", songs = 3),
        )
        whenever(queries.selectArtistAlbums("2")).thenReturn(artistAlbumsQuery)

        sut.observeSiblingsById("1").test(this) {
            assertValue(listOf(
                MediaStoreAlbum(id = "2", songs = 4),
                MediaStoreAlbum(id = "3", songs = 3),
            ))
        }
    }

    @Test
    fun `test observeSiblings, should be null when item is missing`() = runTest {
        val query = QueryOneOrNull<Albums_view>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        sut.observeSiblingsById("1").test(this) {
            assertNoValues()
        }
    }

    @Test
    fun `test observeArtistsAlbums`() = runTest {
        val query = QueryList(AlbumView(id = "1", songs = 2))
        whenever(queries.selectArtistAlbums("1")).thenReturn(query)

        val expected = MediaStoreAlbum(
            id = "1",
            songs = 2,
        )

        sut.observeArtistsAlbums("1").test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getAlbumsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setAlbumsSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailAlbumsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailAlbumsSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailAlbumsSort(sort)
    }


}