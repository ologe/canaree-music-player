package dev.olog.data.mapper

import dev.olog.core.entity.EqualizerBand
import dev.olog.core.entity.EqualizerPreset
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

        val domain = EqualizerPreset(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBand(10.0f, 10.0f)),
            isCustom = true
        )

        // when
        val actual = entity.toDomain()

        // then
        assertEquals(
            domain,
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

        val entity = EqualizerPresetEntity(
            id = 1,
            name = "name",
            bands = listOf(EqualizerBandEntity(10.0f, 10.0f)),
            isCustom = true
        )

        // when
        val actual = domain.toEntity()

        // then
        assertEquals(
            entity,
            actual
        )
    }

}