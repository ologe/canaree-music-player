package dev.olog.data.author

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaStoreArtist
import dev.olog.core.MediaStoreSong
import dev.olog.core.entity.id.AuthorIdentifier
import dev.olog.core.sort.AuthorDetailSort
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.ArtistView
import dev.olog.testing.IndexedPlayables
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStoreArtistRepositoryTest {

    private val queries = mock<ArtistsQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = MediaStoreArtistRepository(
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
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(ArtistView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getById(AuthorIdentifier.MediaStore(1, false))
        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Artists_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getById(AuthorIdentifier.MediaStore(1, false))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(ArtistView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        sut.observeById(AuthorIdentifier.MediaStore(1, false)).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Artists_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeById(AuthorIdentifier.MediaStore(1, false)).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val actual = sut.getPlayablesById(AuthorIdentifier.MediaStore(1, false))
        val expected = MediaStoreSong(id = 1)

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val expected = MediaStoreSong(id = 1)

        sut.observePlayablesById(AuthorIdentifier.MediaStore(1, false)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        sut.observeRecentlyPlayed().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test addRecentlyPlayed`() = runTest {
        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)

        sut.addToRecentlyPlayed(AuthorIdentifier.MediaStore(10, false))

        verify(queries).insertRecentlyPlayed(10, 100)
    }

    @Test
    fun `test observeRecentlyAdded`() = runTest {
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = MediaStoreArtist(
            id = 1,
            songs = 2,
        )

        sut.observeRecentlyAdded().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getArtistsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setArtistsSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailArtistsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailArtistsSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailArtistsSort(sort)
    }

}