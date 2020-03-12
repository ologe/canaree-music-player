package dev.olog.data.db

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.olog.data.model.db.PodcastPositionEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.asExecutor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class PodcastPositionDaoIntegrationTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private lateinit var db: AppDatabase
    private lateinit var sut: PodcastPositionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        sut = db.podcastPositionDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetAndSet() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val positionEntity = PodcastPositionEntity(id, 15000)
        assertNull("should be ull", sut.getPosition(id))

        // when
        sut.setPosition(positionEntity)

        // then
        assertEquals(
            positionEntity.position,
            sut.getPosition(id)
        )
    }

}