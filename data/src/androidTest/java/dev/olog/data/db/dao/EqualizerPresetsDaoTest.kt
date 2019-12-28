package dev.olog.data.db.dao

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import dev.olog.data.db.entities.EqualizerBandEntity
import dev.olog.data.db.entities.EqualizerPresetEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class EqualizerPresetsDaoTest {

    private val mockPreset1 = EqualizerPresetEntity(
        1, "rock", listOf(
            EqualizerBandEntity(10f, 16000f)
        ),
        true
    )

    private val mockPreset2 = mockPreset1.copy(id = 2)

    lateinit var db: AppDatabase
    lateinit var dao: EqualizerPresetsDao

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    @Before
    fun setup() = coroutinesRule.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        dao = db.equalizerPresetsDao()

        dao.insertPresets(listOf(mockPreset1, mockPreset2))
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun testInsertAndGetAll() = coroutinesRule.runBlocking {
        // given
        val presets = listOf(mockPreset1, mockPreset2)

        // when
        val actual = dao.getPresets()

        // then
        assertEquals(presets, actual)
    }

    @Test
    fun shouldReturnItemWhenGetById() {
        // when
        val preset = dao.getPresetById(mockPreset1.id)

        assertEquals(mockPreset1, preset)
    }

    @Test
    fun shouldReturnNullItemWhenGetById() {
        // when
        val preset = dao.getPresetById(-1)

        // then
        assertNull(preset)
    }

    @Test
    fun shouldReturnItemWhenObserveById() = coroutinesRule.runBlocking {
        // when
        val preset = dao.observePresetById(mockPreset1.id)
            .take(1)
            .first()

        // then
        assertEquals(mockPreset1, preset)
    }

    @Test
    fun shouldDeletePreset() = coroutinesRule.runBlocking {
        // when
        dao.deletePreset(mockPreset1)

        // then
        assertEquals(listOf(mockPreset2), dao.getPresets())
    }

    @Test
    fun shouldInsertPreset() = coroutinesRule.runBlocking {
        // given
        val preset = mockPreset1.copy(id = 10)

        // when
        dao.insertPreset(preset)

        // then
        assertEquals(
            listOf(mockPreset1, mockPreset2, preset),
            dao.getPresets()
        )
    }

    @Test
    fun shouldInsertPresets() = coroutinesRule.runBlocking {
        // given
        val presets = listOf(
            mockPreset1.copy(id = 10),
            mockPreset1.copy(id = 11)
        )

        // when
        dao.insertPresets(presets)

        // then
        assertEquals(
            (listOf(mockPreset1, mockPreset2) + presets),
            dao.getPresets()
        )
    }

}