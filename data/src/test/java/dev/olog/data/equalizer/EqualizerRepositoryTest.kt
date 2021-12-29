package dev.olog.data.equalizer

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.preference.Preference
import dev.olog.core.equalizer.EqualizerBand
import dev.olog.core.equalizer.EqualizerPreset
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.mockTransacter
import dev.olog.feature.equalizer.EqualizerPrefs
import dev.olog.flow.test.observer.test
import dev.olog.shared.android.BuildVersion
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EqualizerRepositoryTest {

    private val queries = mock<EqualizerQueries>()
    private val prefs = mock<EqualizerPrefs>()
    private val buildVersion = mock<BuildVersion>()
    private val sut = EqualizerRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        prefs = prefs,
        buildVersion = buildVersion,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getPresets api 28`() {
        whenever(buildVersion.isPie()).thenReturn(true)

        val defaultPresetsQuery =
            QueryList(Equalizer_preset(id = 1, name = "name", custom = false, api_level = 28))
        whenever(queries.selectDefaultPresetsApi28()).thenReturn(defaultPresetsQuery)
        val defaultPresetsValuesQuery =
            QueryList(Equalizer_preset_value(10, 10f, 100f, preset_id = 1))
        whenever(queries.selectPresetValues(1)).thenReturn(defaultPresetsValuesQuery)

        val userPresetsQuery =
            QueryList(Equalizer_preset(id = 2, name = "custom", custom = true, api_level = null))
        whenever(queries.selectCustomPresets()).thenReturn(userPresetsQuery)
        val userPresetsValuesQuery = QueryList(Equalizer_preset_value(20, 20f, 200f, preset_id = 1))
        whenever(queries.selectPresetValues(2)).thenReturn(userPresetsValuesQuery)

        val actual = sut.getPresets()
        val expected = listOf(
            EqualizerPreset(
                id = 1,
                name = "name",
                isCustom = false,
                bands = listOf(
                    EqualizerBand(10, 10f, 100f)
                )
            ),
            EqualizerPreset(
                id = 2,
                name = "custom",
                isCustom = true,
                bands = listOf(
                    EqualizerBand(20, 20f, 200f)
                )
            ),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getPresets pre api 28`() {
        whenever(buildVersion.isPie()).thenReturn(false)

        val defaultPresetsQuery =
            QueryList(Equalizer_preset(id = 1, name = "name", custom = false, api_level = 28))
        whenever(queries.selectDefaultPresetsPreApi28()).thenReturn(defaultPresetsQuery)
        val defaultPresetsValuesQuery =
            QueryList(Equalizer_preset_value(10, 10f, 100f, preset_id = 1))
        whenever(queries.selectPresetValues(1)).thenReturn(defaultPresetsValuesQuery)

        val userPresetsQuery =
            QueryList(Equalizer_preset(id = 2, name = "custom", custom = true, api_level = null))
        whenever(queries.selectCustomPresets()).thenReturn(userPresetsQuery)
        val userPresetsValuesQuery = QueryList(Equalizer_preset_value(20, 20f, 200f, preset_id = 1))
        whenever(queries.selectPresetValues(2)).thenReturn(userPresetsValuesQuery)

        val actual = sut.getPresets()
        val expected = listOf(
            EqualizerPreset(
                id = 1,
                name = "name",
                isCustom = false,
                bands = listOf(
                    EqualizerBand(10, 10f, 100f)
                )
            ),
            EqualizerPreset(
                id = 2,
                name = "custom",
                isCustom = true,
                bands = listOf(
                    EqualizerBand(20, 20f, 200f)
                )
            ),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getCurrentPreset`() {
        val preference = mock<Preference<Long>> { on { get() } doReturn 1L }
        whenever(prefs.currentPresetId).thenReturn(preference)

        val query = QueryOne(Equalizer_preset(1, "name", false, null))
        whenever(queries.selectPresetById(1)).thenReturn(query)
        val valuesQuery = QueryList(Equalizer_preset_value(10, 100f, 1000f, 1))
        whenever(queries.selectPresetValues(1)).thenReturn(valuesQuery)

        val actual = sut.getCurrentPreset()
        val expected = EqualizerPreset(
            id = 1,
            name = "name",
            isCustom = false,
            bands = listOf(
                EqualizerBand(10, 100f, 1000f)
            )
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test observeCurrentPreset`() = runTest {
        val preference = mock<Preference<Long>> { on { observe() } doReturn flowOf(1L) }
        whenever(prefs.currentPresetId).thenReturn(preference)

        val query = QueryOne(Equalizer_preset(1, "name", false, null))
        whenever(queries.selectPresetById(1)).thenReturn(query)
        val valuesQuery = QueryList(Equalizer_preset_value(10, 100f, 1000f, 1))
        whenever(queries.selectPresetValues(1)).thenReturn(valuesQuery)

        sut.observeCurrentPreset().test(this) {
            assertValue(
                EqualizerPreset(
                    id = 1,
                    name = "name",
                    isCustom = false,
                    bands = listOf(
                        EqualizerBand(10, 100f, 1000f)
                    )
                )
            )
        }
    }

    @Test
    fun `test addPreset`() = runTest {
        val query = QueryOne(1L)
        whenever(queries.selectLastInsertedRowId()).thenReturn(query)

        val bands = listOf(
            EqualizerBand(10, 100f, 1000f),
            EqualizerBand(20, 200f, 2000f),
        )
        sut.addPreset("title", bands)

        verify(queries).insertPreset("title")
        verify(queries).insertPresetValue(preset_id = 1, gain = 100f, frequency = 1000f)
        verify(queries).insertPresetValue(preset_id = 1, gain = 200f, frequency = 2000f)
    }

    @Test
    fun `test updatePreset`() = runTest {
        sut.updatePreset(
            EqualizerPreset(
                id = 1L,
                name = "name",
                isCustom = true,
                bands = listOf(
                    EqualizerBand(id = 10, gain = 100f, frequency = 1000f),
                    EqualizerBand(id = 20, gain = 200f, frequency = 2000f),
                )
            )
        )

        verify(queries).updatePreset(name = "name", id = 1)
        verify(queries).updatePresetValue(gain = 100f, frequency = 1000f, id = 10L)
        verify(queries).updatePresetValue(gain = 200f, frequency = 2000f, id = 20L)
    }

    @Test
    fun `test deletePreset`() = runTest {
        sut.deletePreset(1)

        verify(queries).deletePresetValues(1)
        verify(queries).deletePreset(1)
    }

}