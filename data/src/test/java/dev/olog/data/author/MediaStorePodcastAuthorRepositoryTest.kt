package dev.olog.data.author

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaStorePodcastAuthor
import dev.olog.core.MediaStorePodcastEpisode
import dev.olog.core.sort.AuthorDetailSort
import dev.olog.core.sort.AuthorSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.IndexedTrack
import dev.olog.testing.PodcastAuthorView
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStorePodcastAuthorRepositoryTest {

    private val queries = mock<PodcastAuthorsQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = MediaStorePodcastAuthorRepository(
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
        val query = QueryList(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = MediaStorePodcastAuthor(
            id = "1",
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStorePodcastAuthor(
            id = "1",
            songs = 2,
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = sut.getById("1")
        val expected = MediaStorePodcastAuthor(
            id = "1",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Podcast_authors_view>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = sut.getById("1")
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectById("1")).thenReturn(query)

        val expected = MediaStorePodcastAuthor(
            id = "1",
            songs = 2,
        )

        sut.observeById("1").test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Podcast_authors_view>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        sut.observeById("1").test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedTrack(id = "1", is_podcast = true))
        whenever(queries.selectTracksByIdSorted("1")).thenReturn(query)

        val actual = sut.getTracksById("1")
        val expected = MediaStorePodcastEpisode(id = "1")

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedTrack(id = "1", is_podcast = true))
        whenever(queries.selectTracksByIdSorted("1")).thenReturn(query)

        val expected = MediaStorePodcastEpisode(id = "1")

        sut.observeTracksById("1").test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = MediaStorePodcastAuthor(
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
        val query = QueryList(PodcastAuthorView(id = "1", episodes = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = MediaStorePodcastAuthor(
            id = "1",
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