package dev.olog.data.history

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaStorePodcastEpisode
import dev.olog.core.MediaStoreSong
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.data.HistoryQueries
import dev.olog.data.extensions.QueryList
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.IndexedPlayables
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HistoryRepositoryTest {

    private val queries = mock<HistoryQueries>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = HistoryRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        dateTimeFactory = dateTimeFactory,
    )

    @Test
    fun `test getSongs`() {
        val query = QueryList(IndexedPlayables(1, false))
        whenever(queries.selectAllSongs()).thenReturn(query)

        val actual = sut.getSongs()
        val expected = MediaStoreSong(id = 1L)

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test getPodcastEpisodes`() {
        val query = QueryList(IndexedPlayables(1, true))
        whenever(queries.selectAllPodcastEpisodes()).thenReturn(query)

        val actual = sut.getPodcastEpisodes()
        val expected = MediaStorePodcastEpisode(id = 1L)

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeSongs`() = runTest {
        val query = QueryList(IndexedPlayables(1, false))
        whenever(queries.selectAllSongs()).thenReturn(query)

        val expected = MediaStoreSong(id = 1L)
        sut.observeSongs().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observePodcastEpisodes`() = runTest {
        val query = QueryList(IndexedPlayables(1, true))
        whenever(queries.selectAllPodcastEpisodes()).thenReturn(query)

        val expected = MediaStorePodcastEpisode(id = 1L)
        sut.observePodcastEpisodes().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test insert`() = runTest {
        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)

        sut.insert(PlayableIdentifier.MediaStore(1, true))

        verify(queries).insert(1, 100)
    }

}