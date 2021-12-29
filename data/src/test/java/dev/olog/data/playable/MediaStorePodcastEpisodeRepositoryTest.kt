package dev.olog.data.playable

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaStorePodcastEpisode
import dev.olog.core.sort.TrackSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.core.track.Song
import dev.olog.data.PodcastPositionQueries
import dev.olog.data.Podcast_position
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.emptyIndexedPlayables
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MediaStorePodcastEpisodeRepositoryTest {

    private val queries = mock<PodcastEpisodesQueries>()
    private val sortDao = mock<SortDao>()
    private val podcastPositionQueries = mock<PodcastPositionQueries>()
    private val repo = MediaStorePodcastEpisodeRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        sortDao = sortDao,
        podcastPositionQueries = podcastPositionQueries,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll`() {
        val query = QueryList(
            emptyIndexedPlayables().copy(id = "1", is_podcast = true),
            emptyIndexedPlayables().copy(id = "2", is_podcast = true),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = repo.getAll()
        val expected = listOf(
            MediaStorePodcastEpisode(id = "1"),
            MediaStorePodcastEpisode(id = "2"),
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(
            emptyIndexedPlayables().copy(id = "1", is_podcast = true),
            emptyIndexedPlayables().copy(id = "2", is_podcast = true),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = listOf(
            MediaStorePodcastEpisode(id = "1"),
            MediaStorePodcastEpisode(id = "2"),
        )

        repo.observeAll().test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = "1", is_podcast = true),
        )
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = repo.getById("1")
        val expected = MediaStorePodcastEpisode(id = "1")

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        val actual = repo.getById("1")
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = "1", is_podcast = true),
        )
        whenever(queries.selectById("1")).thenReturn(query)

        val expected = MediaStorePodcastEpisode(id = "1")

        repo.observeById("1").test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById("1")).thenReturn(query)

        repo.observeById("1").test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getByCollectionId`() {
        val query = QueryList(
            emptyIndexedPlayables().copy(collection_id = "1", is_podcast = true),
        )
        whenever(queries.selectByCollectionId("1")).thenReturn(query)

        val actual = repo.getByCollectionId("1")
        val expected = MediaStorePodcastEpisode(collectionId = "1")

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test getByCollectionId, missing item should return empty list`() {
        val query = QueryList<Indexed_playables>(emptyList())
        whenever(queries.selectByCollectionId("1")).thenReturn(query)

        val actual = repo.getByCollectionId("1")
        Assert.assertEquals(emptyList<Song>(), actual)
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(TrackSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getPodcastEpisodesSort()).thenReturn(query)

        val actual = repo.getSort()
        Assert.assertEquals(sort, actual)
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(TrackSort.Title, SortDirection.ASCENDING)
        repo.setSort(sort)
        verify(sortDao).setPodcastEpisodesSort(sort)
    }

    @Test
    fun `test saveCurrentPosition`() {
        repo.saveCurrentPosition("1", 10)
        verify(podcastPositionQueries).insert("1", 10L)
    }

    @Test
    fun `test getCurrentPosition`() {
        val bookmark = 1.minutes.inWholeMilliseconds
        val duration = 2.minutes.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position("1", bookmark))
        whenever(podcastPositionQueries.selectById("1")).thenReturn(query)

        val actual = repo.getCurrentPosition("1", duration)
        Assert.assertEquals(bookmark, actual)
    }

    @Test
    fun `test getCurrentPosition, should coerce to 0 when less than 0`() {
        val duration = 2.minutes.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position("1", -1))
        whenever(podcastPositionQueries.selectById("1")).thenReturn(query)

        val actual = repo.getCurrentPosition("1", duration)
        Assert.assertEquals(0, actual)
    }

    @Test
    fun `test getCurrentPosition, restart is less than 5 seconds to finish`() {
        val duration = 2.minutes.inWholeMilliseconds
        val bookmark = duration - 4.seconds.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position("1", bookmark))
        whenever(podcastPositionQueries.selectById("1")).thenReturn(query)

        val actual = repo.getCurrentPosition("1", duration)
        Assert.assertEquals(0L, actual)
    }

}