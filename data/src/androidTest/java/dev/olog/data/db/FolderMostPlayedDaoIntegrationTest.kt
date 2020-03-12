package dev.olog.data.db

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.data.MocksIntegration
import dev.olog.data.model.db.FolderMostPlayedEntity
import dev.olog.data.model.db.MostTimesPlayedSongEntity
import dev.olog.test.shared.MainCoroutineRule
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

class FolderMostPlayedDaoIntegrationTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private lateinit var db: AppDatabase
    private lateinit var sut: FolderMostPlayedDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        sut = db.folderMostPlayedDao()
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
        val query = "/storage/emulated/0/folder"
        assertTrue("should be empty", sut.query(query).first().isEmpty())

        // when
        val item = FolderMostPlayedEntity(1, songId, query)
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
            sut.query(query).first()
        )
    }

    @Test
    fun testGetAll() = coroutinesRule.runBlockingTest {
        // given
        val songGateway = mock<TrackGateway>()
        whenever(songGateway.getAllTracks()).thenReturn(
            listOf(
                MocksIntegration.song.copy(id = 1),
                MocksIntegration.song.copy(id = 2),
                MocksIntegration.song.copy(id = 3)
            )
        )

        val query = "/storage/emulated/0/folder"

        val item = FolderMostPlayedEntity(1, 10, query)
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
        val actual = sut.getAll(query, songGateway).first()

        // then
        val expected = listOf(MocksIntegration.song.copy(id = 1))
        assertEquals(
            expected,
            actual
        )
    }

}