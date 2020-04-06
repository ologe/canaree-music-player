package dev.olog.data.mapper

import dev.olog.domain.entity.EqualizerBand
import dev.olog.domain.entity.EqualizerPreset
import dev.olog.data.model.db.EqualizerBandEntity
import dev.olog.data.model.db.EqualizerPresetEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class EqualizerPresetMapperKtTest {

    @Test
    fun testToDomain() {
        // given
        val entity = EqualizerPresetEntity(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBandEntity(10.0f, 10.0f)),
            isCustom = true
        )

        // when
        val actual = entity.toDomain()

        // then
        val expected = EqualizerPreset(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBand(10.0f, 10.0f)),
            isCustom = true
        )

        assertEquals(
            expected,
            actual
        )
    }

    @Test
    fun testToEntity() {
        // given
        val domain = EqualizerPreset(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBand(10.0f, 10.0f)),
            isCustom = true
        )

        // when
        val actual = domain.toEntity()

        // then
        val expected = EqualizerPresetEntity(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBandEntity(10.0f, 10.0f)),
            isCustom = true
        )

        assertEquals(
            expected,
            actual
        )
    }

}