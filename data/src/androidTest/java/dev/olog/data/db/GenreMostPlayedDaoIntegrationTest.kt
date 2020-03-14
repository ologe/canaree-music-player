package dev.olog.data.db

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.model.db.GenreMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.Mocks
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class GenreMostPlayedDaoIntegrationTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private lateinit var db: AppDatabase
    private lateinit var sut: GenreMostPlayedDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        sut = db.genreMostPlayedDao()
    }

    @After
    @Throws(IOException::class)
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
    fun testGetAll() = coroutinesRule.runBlockingTest {
        // given
        val songGateway = mock<TrackGateway>()
        whenever(songGateway.getAllTracks()).thenReturn(
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
        val actual = sut.getAll(genreId, songGateway).first()

        // then
        val expected = listOf(Mocks.song.copy(id = 1))
        assertEquals(
            expected,
            actual
        )
    }

}