package dev.olog.data.equalizer

import dev.olog.data.TestDatabase
import org.junit.Assert
import org.junit.Test

class EqualizerQueriesTest {

    private val db = TestDatabase()
    private val queries = db.equalizerQueries

    @Test
    fun `test default api 28`() {
        val actual = queries.selectPresets {
            queries.selectDefaultPresetsApi28().executeAsList()
        }
        val expected = EqualizerTestModels.createDefaultPresetsApi28

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test default pre api 28`() {
        val actual = queries.selectPresets {
            queries.selectDefaultPresetsPreApi28().executeAsList()
        }
        val expected = EqualizerTestModels.createDefaultPresetsPreApi28
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectPresetValues`() {
        val actual = queries.selectPresetValues(2).executeAsList()
        val expected = listOf(
            Equalizer_preset_value(11, 5.0f, 32.0f, 2),
            Equalizer_preset_value(12, 5.0f, 64.0f, 2),
            Equalizer_preset_value(13, 4.0f, 125.0f, 2),
            Equalizer_preset_value(14, 1.0f, 250.0f, 2),
            Equalizer_preset_value(15, 1.5f, 500.0f, 2),
            Equalizer_preset_value(16, 1.5f, 1000.0f, 2),
            Equalizer_preset_value(17, 3.5f, 2000.0f, 2),
            Equalizer_preset_value(18, 4.0f, 4000.0f, 2),
            Equalizer_preset_value(19, 3.5f, 8000.0f, 2),
            Equalizer_preset_value(20, 2.0f, 16000.0f, 2),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectPresetValues, empty when preset id is missing`() {
        val actual = queries.selectPresetValues(0).executeAsList()
        Assert.assertEquals(emptyList<Equalizer_preset_value>(), actual)
    }

    @Test
    fun `test selectCustomPresets`() {
        queries.insertPreset("custom 1")
        queries.insertPreset("custom 2")

        val actual = queries.selectCustomPresets().executeAsList()
        val expected = listOf(
            Equalizer_preset(30, "custom 1", true, null),
            Equalizer_preset(31, "custom 2", true, null),
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test selectPresetById`() {
        Assert.assertEquals(
            Equalizer_preset(1, "Flat", false, 28),
            queries.selectPresetById(1).executeAsOne()
        )

        queries.insertPreset("custom 1")
        Assert.assertEquals(
            Equalizer_preset(30, "custom 1", true, null),
            queries.selectPresetById(30).executeAsOne()
        )
    }

    @Test
    fun `test selectPresetById, null when not present`() {
        Assert.assertEquals(
            null,
            queries.selectPresetById(0).executeAsOneOrNull()
        )
    }

    @Test
    fun `test selectLastInsertedPresetRowId, should be latest default preset id`() {
        queries.insertPreset("custom 1")

        Assert.assertEquals(
            30,
            queries.selectLastInsertedRowId().executeAsOne()
        )
    }

    @Test
    fun `test insertPresetValue`() {
        queries.insertPreset("custom 1")
        val presetId = queries.selectLastInsertedRowId().executeAsOne()

        queries.insertPresetValue(presetId, 10f, 100f)
        queries.insertPresetValue(presetId, 20f, 200f)
        val id = queries.selectLastInsertedRowId().executeAsOne()

        Assert.assertEquals(
            listOf(
                Equalizer_preset_value(id - 1, 10f, 100f, presetId),
                Equalizer_preset_value(id, 20f, 200f, presetId),
            ),
            queries.selectPresetValues(presetId).executeAsList()
        )
    }

    @Test
    fun `test updatePreset`() {
        queries.insertPreset("custom 1")
        val presetId = queries.selectLastInsertedRowId().executeAsOne()

        Assert.assertEquals(
            Equalizer_preset(presetId, "custom 1", true, null),
            queries.selectPresetById(presetId).executeAsOne()
        )

        // when
        queries.updatePreset("updated", presetId)

        // then
        Assert.assertEquals(
            Equalizer_preset(presetId, "updated", true, null),
            queries.selectPresetById(presetId).executeAsOne()
        )
    }

    @Test
    fun `test updatePresetValue`() {
        queries.insertPreset("custom 1")
        val presetId = queries.selectLastInsertedRowId().executeAsOne()
        queries.insertPresetValue(presetId, 10f, 100f)
        val presetValueId = queries.selectLastInsertedRowId().executeAsOne()

        Assert.assertEquals(
            listOf(
                Equalizer_preset_value(presetValueId, 10f, 100f, presetId)
            ),
            queries.selectPresetValues(presetId).executeAsList()
        )

        // when
        queries.updatePresetValue(20f, 200f, presetValueId)

        // then
        Assert.assertEquals(
            listOf(
                Equalizer_preset_value(presetValueId, 20f, 200f, presetId)
            ),
            queries.selectPresetValues(presetId).executeAsList()
        )
    }

    @Test
    fun `test deletePreset`() {
        queries.insertPreset("custom 1")
        val presetId = queries.selectLastInsertedRowId().executeAsOne()

        Assert.assertEquals(
            Equalizer_preset(presetId, "custom 1", true, null),
            queries.selectPresetById(presetId).executeAsOne(),
        )

        // when
        queries.deletePreset(presetId)

        // then
        Assert.assertEquals(
            null,
            queries.selectPresetById(presetId).executeAsOneOrNull(),
        )
    }

    @Test
    fun `test deletePresetValue`() {
        queries.insertPreset("custom 1")
        val presetId = queries.selectLastInsertedRowId().executeAsOne()
        queries.insertPresetValue(presetId, 10f, 100f)
        queries.insertPresetValue(presetId, 20f, 200f)
        val presetValueId = queries.selectLastInsertedRowId().executeAsOne()

        Assert.assertEquals(
            listOf(
                Equalizer_preset_value(presetValueId - 1, 10f, 100f, presetId),
                Equalizer_preset_value(presetValueId, 20f, 200f, presetId),
            ),
            queries.selectPresetValues(presetId).executeAsList(),
        )

        // when
        queries.deletePresetValues(presetId)

        // then
        Assert.assertEquals(
            emptyList<Equalizer_preset_value>(),
            queries.selectPresetValues(presetId).executeAsList(),
        )
    }

}