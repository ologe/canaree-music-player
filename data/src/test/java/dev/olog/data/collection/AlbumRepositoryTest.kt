package dev.olog.data.collection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.entity.sort.CollectionDetailSort
import dev.olog.core.entity.sort.CollectionSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.data.AlbumView
import dev.olog.data.IndexedPlayables
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

class AlbumRepositoryTest {

    private val queries = mock<AlbumsQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = AlbumRepository(
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
        val query = QueryList(AlbumView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(AlbumView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(AlbumView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Albums_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(AlbumView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        sut.observeByParam(1).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Albums_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeByParam(1).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val actual = sut.getTrackListByParam(1)
        val expected = Song(
            id = 1,
            artistId = 0,
            albumId = 0,
            title = "",
            artist = "",
            albumArtist = "",
            album = "",
            duration = 0,
            dateAdded = 0,
            directory = "",
            path = "",
            discNumber = 0,
            trackNumber = 0,
            idInPlaylist = 0,
            isPodcast = false
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val expected = Song(
            id = 1,
            artistId = 0,
            albumId = 0,
            title = "",
            artist = "",
            albumArtist = "",
            album = "",
            duration = 0,
            dateAdded = 0,
            directory = "",
            path = "",
            discNumber = 0,
            trackNumber = 0,
            idInPlaylist = 0,
            isPodcast = false
        )

        sut.observeTrackListByParam(1).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(AlbumView(id = 1, songs = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        sut.observeRecentlyPlayed().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test addRecentlyPlayed`() = runTest {
        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)

        sut.addRecentlyPlayed(10)

        verify(queries).insertRecentlyPlayed(10, 100)
    }

    @Test
    fun `test observeRecentlyAdded`() = runTest {
        val query = QueryList(AlbumView(id = 1, songs = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = Album(
            id = 1,
            songs = 2,
            artistId = 0,
            title = "",
            artist = "",
            directory = "",
            isPodcast = false
        )

        sut.observeRecentlyAdded().test(this) {
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