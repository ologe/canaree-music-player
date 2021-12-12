package dev.olog.data.author

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.entity.sort.AuthorDetailSort
import dev.olog.core.entity.sort.AuthorSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.data.IndexedPlayables
import dev.olog.data.PodcastAuthorView
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

class PodcastAuthorRepositoryTest {

    private val queries = mock<PodcastAuthorsQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = PodcastAuthorRepository(
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
        val query = QueryList(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Podcast_authors_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
        )

        sut.observeByParam(1).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Podcast_authors_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeByParam(1).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = true))
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
            isPodcast = true
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = true))
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
            isPodcast = true
        )

        sut.observeTrackListByParam(1).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
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
        val query = QueryList(PodcastAuthorView(id = 1, episodes = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = Artist(
            id = 1,
            name = "",
            songs = 2,
            isPodcast = true
        )

        sut.observeRecentlyAdded().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getPodcastAuthorsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(AuthorSort.Name, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setPodcastAuthorsSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailPodcastAuthorsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailPodcastAuthorsSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(AuthorDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailPodcastAuthorsSort(sort)
    }

}