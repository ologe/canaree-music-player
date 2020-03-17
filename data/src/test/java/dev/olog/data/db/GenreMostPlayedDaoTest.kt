package dev.olog.data.db

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.DatabaseBuilder
import dev.olog.data.model.db.GenreMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.Mocks
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class GenreMostPlayedDaoTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val db by lazy { DatabaseBuilder.build(coroutinesRule.testDispatcher) }
    private val sut by lazy { db.genreMostPlayedDao() }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndQuery() = coroutinesRule.runBlockingTest {
        // given
        val songId = 10L
        val genreId = 1L
        assertTrue("should be empty", sut.query(genreId).first().isEmpty())

        // when
        val item = GenreMostPlayedEntity(1, songId, genreId)
        sut.insert(
            item.copy(id = 1),
            item.copy(id = 2),
            item.copy(id = 3),
            item.copy(id = 4),
            item.copy(id = 5)
        )

        // then
        assertEquals(
            listOf(MostTimesPlayedSongEntity(songId = songId, timesPlayed = 5)),
            sut.query(genreId).first()
        )
    }

    @Test
    fun testObserveAll() = coroutinesRule.runBlockingTest {
        // given
        val trackGateway = mock<TrackGateway>()
        whenever(trackGateway.getAllTracks()).thenReturn(
            listOf(
                Mocks.song.copy(id = 1),
                Mocks.song.copy(id = 2),
                Mocks.song.copy(id = 3)
            )
        )

        val genreId = 1L

        val item = GenreMostPlayedEntity(1, 10, genreId)
        sut.insert(
            // in song gateway
            item.copy(id = 1, songId = 1),
            item.copy(id = 2, songId = 1),
            item.copy(id = 3, songId = 1),
            item.copy(id = 4, songId = 1),
            item.copy(id = 5, songId = 1),
            // not enough plays
            item.copy(id = 6, songId = 2),
            item.copy(id = 7, songId = 2),
            item.copy(id = 8, songId = 2),
            item.copy(id = 9, songId = 2),
            // not in song gateway
            item.copy(id = 10, songId = 100)
        )

        // when
        val actual = sut.observeAll(genreId, trackGateway).first()

        // then
        val expected = listOf(Mocks.song.copy(id = 1))
        assertEquals(
            expected,
            actual
        )
    }

}