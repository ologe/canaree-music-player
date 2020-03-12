package dev.olog.data.db

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.olog.data.model.db.OfflineLyricsEntity
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

class OfflineLyricsDaoIntegrationTest {

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private lateinit var db: AppDatabase
    private lateinit var sut: OfflineLyricsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        sut = db.offlineLyricsDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun testSaveAndObserve() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        val lyrics = OfflineLyricsEntity(id, "lyrics")
        assertTrue("should be empty", sut.observeLyrics(id).first().isEmpty())

        // when
        sut.saveLyrics(lyrics)

        // then
        assertEquals(
            listOf(lyrics),
            sut.observeLyrics(id).first()
        )
    }

}