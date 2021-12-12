package dev.olog.data.history

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.entity.track.Song
import dev.olog.data.HistoryQueries
import dev.olog.data.IndexedPlayables
import dev.olog.data.extensions.QueryList
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class HistoryRepositoryTest {

    private val queries = mock<HistoryQueries>()
    private val sut = HistoryRepository(
        schedulers = TestSchedulers(),
        queries = queries,
    )

    @Test
    fun `test getSongs`() {
        val query = QueryList(IndexedPlayables(1, false))
        whenever(queries.selectAllSongs()).thenReturn(query)

        val actual = sut.getSongs()
        val expected = Song(
            id = 1L,
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
    fun `test getPodcastEpisodes`() {
        val query = QueryList(IndexedPlayables(1, true))
        whenever(queries.selectAllPodcastEpisodes()).thenReturn(query)

        val actual = sut.getPodcastEpisodes()
        val expected = Song(
            id = 1L,
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
    fun `test observeSongs`() = runTest {
        val query = QueryList(IndexedPlayables(1, false))
        whenever(queries.selectAllSongs()).thenReturn(query)

        val expected = Song(
            id = 1L,
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
        sut.observeSongs().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observePodcastEpisodes`() = runTest {
        val query = QueryList(IndexedPlayables(1, true))
        whenever(queries.selectAllPodcastEpisodes()).thenReturn(query)

        val expected = Song(
            id = 1L,
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
        sut.observePodcastEpisodes().test(this) {
            assertValue(listOf(expected))
        }
    }

}