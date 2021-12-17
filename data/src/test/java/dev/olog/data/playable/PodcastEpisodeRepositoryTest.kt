package dev.olog.data.playable

import com.nhaarman.mockitokotlin2.*
import dev.olog.core.EMPTY
import dev.olog.core.entity.sort.PlayableSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.track.Song
import dev.olog.data.PodcastPositionQueries
import dev.olog.data.Podcast_position
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.index.IndexedPlayablesQueries
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.emptyIndexedPlayables
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PodcastEpisodeRepositoryTest {

    private val queries = mock<PodcastEpisodesQueries>()
    private val sortDao = mock<SortDao>()
    private val indexedPlayablesQueries = mock<IndexedPlayablesQueries>()
    private val playableOperations = mock<PlayableOperations>()
    private val podcastPositionQueries = mock<PodcastPositionQueries>()
    private val repo = PodcastEpisodeRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        sortDao = sortDao,
        indexedPlayablesQueries = indexedPlayablesQueries,
        playableOperations = playableOperations,
        podcastPositionQueries = podcastPositionQueries,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll`() {
        val query = QueryList(
            emptyIndexedPlayables().copy(id = 1L),
            emptyIndexedPlayables().copy(id = 2L),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = repo.getAll()
        val expected = listOf(
            Song.EMPTY.copy(id = 1L),
            Song.EMPTY.copy(id = 2L),
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(
            emptyIndexedPlayables().copy(id = 1L),
            emptyIndexedPlayables().copy(id = 2L),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = listOf(
            Song.EMPTY.copy(id = 1L),
            Song.EMPTY.copy(id = 2L),
        )

        repo.observeAll().test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = 1L),
        )
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = repo.getByParam(1)
        val expected = Song.EMPTY.copy(id = 1L)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = repo.getByParam(1)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = 1L),
        )
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = Song.EMPTY.copy(id = 1L)

        repo.observeByParam(1).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        repo.observeByParam(1).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getByAlbumId`() {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(collection_id = 1L),
        )
        whenever(queries.selectByCollectionId(1)).thenReturn(query)

        val actual = repo.getByCollectionId(1)
        val expected = Song.EMPTY.copy(albumId = 1L)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByAlbumId, missing item should return null`() {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectByCollectionId(1)).thenReturn(query)

        val actual = repo.getByCollectionId(1)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test deleteSingle`() = runTest {
        val playable = Song.EMPTY.copy(id = 1)
        val query = QueryOneOrNull(emptyIndexedPlayables().copy(id = 1))
        whenever(queries.selectById(1)).thenReturn(query)
        whenever(playableOperations.delete(playable)).thenReturn(2)

        repo.deleteSingle(1)

        verify(playableOperations).delete(playable)
        verify(indexedPlayablesQueries).delete(2)
    }

    @Test
    fun `test deleteSingle, missing item`() = runTest {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById(1)).thenReturn(query)
        whenever(playableOperations.delete(null)).thenReturn(null)

        repo.deleteSingle(1)

        verify(playableOperations, never()).delete(any())
        verify(indexedPlayablesQueries, never()).delete(any())
    }

    @Test
    fun `test deleteGroup`() = runTest {
        val playables = listOf(
            Song.EMPTY.copy(id = 1),
            Song.EMPTY.copy(id = 2),
            Song.EMPTY.copy(id = 3),
        )
        val query = QueryList(
            emptyIndexedPlayables().copy(id = 1),
            emptyIndexedPlayables().copy(id = 2),
        )
        whenever(queries.selectById(1)).thenReturn(query)
        whenever(playableOperations.delete(playables[0])).thenReturn(10)
        whenever(playableOperations.delete(playables[1])).thenReturn(null) // should be skipped
        whenever(playableOperations.delete(playables[2])).thenReturn(20)

        repo.deleteGroup(playables)

        verify(playableOperations).delete(playables[0])
        verify(playableOperations).delete(playables[1])
        verify(playableOperations).delete(playables[2])
        verify(indexedPlayablesQueries).delete(10)
        verify(indexedPlayablesQueries).delete(20)
    }

    @Test
    fun `test getByUri`() {
        val uri = URI.create("canaree:track:id")
        whenever(playableOperations.getByUri(uri)).thenReturn(1)
        val query = QueryOneOrNull(emptyIndexedPlayables().copy(id = 1))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = repo.getByUri(uri)
        val expected = Song.EMPTY.copy(id = 1L)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByUri, missing item`() {
        val uri = URI.create("canaree:track:id")
        whenever(playableOperations.getByUri(uri)).thenReturn(null)

        val actual = repo.getByUri(uri)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(PlayableSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getPodcastEpisodesSort()).thenReturn(query)

        val actual = repo.getSort()
        Assert.assertEquals(sort, actual)
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(PlayableSort.Title, SortDirection.ASCENDING)
        repo.setSort(sort)
        verify(sortDao).setPodcastEpisodesSort(sort)
    }

    @Test
    fun `test saveCurrentPosition`() {
        repo.saveCurrentPosition(1, 10)
        verify(podcastPositionQueries).insert(1L, 10L)
    }

    @Test
    fun `test getCurrentPosition`() {
        val bookmark = 1.minutes.inWholeMilliseconds
        val duration = 2.minutes.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position(1, bookmark))
        whenever(podcastPositionQueries.selectById(1)).thenReturn(query)

        val actual = repo.getCurrentPosition(1, duration)
        Assert.assertEquals(bookmark, actual)
    }

    @Test
    fun `test getCurrentPosition, should coerce to 0 when less than 0`() {
        val duration = 2.minutes.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position(1, -1))
        whenever(podcastPositionQueries.selectById(1)).thenReturn(query)

        val actual = repo.getCurrentPosition(1, duration)
        Assert.assertEquals(0, actual)
    }

    @Test
    fun `test getCurrentPosition, restart is less than 5 seconds to finish`() {
        val duration = 2.minutes.inWholeMilliseconds
        val bookmark = duration - 4.seconds.inWholeMilliseconds
        val query = QueryOneOrNull(Podcast_position(1, bookmark))
        whenever(podcastPositionQueries.selectById(1)).thenReturn(query)

        val actual = repo.getCurrentPosition(1, duration)
        Assert.assertEquals(0L, actual)
    }

}