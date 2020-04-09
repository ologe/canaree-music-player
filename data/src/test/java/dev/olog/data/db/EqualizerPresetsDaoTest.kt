package dev.olog.lib.db

import dev.olog.lib.DatabaseBuilder
import dev.olog.lib.model.db.EqualizerBandEntity
import dev.olog.lib.model.db.EqualizerPresetEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class EqualizerPresetsDaoTest {

    private val mockData = EqualizerPresetEntity(
        1L, "preset", listOf(
            EqualizerBandEntity(10f, 16000f)
        ), false
    )

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val db by lazy { DatabaseBuilder.build(coroutinesRule.testDispatcher) }
    private val sut by lazy { db.equalizerPresetsDao() }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetPresets() = coroutinesRule.runBlockingTest {
        // given
        sut.insertPresets(mockData)

        // when
        val actual = sut.getPresets()

        // then
        val expected = listOf(mockData)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetPresetsById() = coroutinesRule.runBlockingTest {
        // given
        val id = 10L
        val inserted = mockData.copy(id = id)
        sut.insertPresets(inserted)

        // when
        val actual = sut.getPresetById(id)

        // then
        assertEquals(inserted, actual)
    }

    @Test
    fun testGetPresetsByIdReturnNull() = coroutinesRule.runBlockingTest {
        // given
        val id = 10L
        sut.insertPresets(mockData.copy(id = 1L))

        // when
        val item = sut.getPresetById(id)

        // then
        assertNull(item)
    }

    @Test
    fun testObservePresets() = coroutinesRule.runBlockingTest {
        // given
        val id = 1L
        sut.insertPresets(mockData)

        // when
        val actual = sut.observePresetById(id).first()

        // then
        assertEquals(mockData, actual)
    }

    @Test
    fun testDeletePresets() = coroutinesRule.runBlockingTest {
        // given
        sut.insertPresets(mockData)

        // when
        sut.deletePreset(mockData)

        // then
        assertTrue("should be empty", sut.getPresets().isEmpty())
    }

    @Test
    fun testInsertPresets() = coroutinesRule.runBlockingTest {
        // given
        assertTrue("should be empty", sut.getPresets().isEmpty())

        // when
        sut.insertPresets(mockData)

        // then
        assertEquals(
            listOf(mockData),
            sut.getPresets()
        )
    }

    @Test
    fun testReplacePresets() = coroutinesRule.runBlockingTest {
        // given
        assertTrue("should be empty", sut.getPresets().isEmpty())
        sut.insertPresets(mockData.copy(isCustom = false))

        // when
        val newItem = mockData.copy(isCustom = true)
        sut.insertPresets(newItem)

        // then
        assertEquals(
            listOf(newItem),
            sut.getPresets()
        )
    }

}