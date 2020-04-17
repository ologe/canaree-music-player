package dev.olog.data.db

import dev.olog.data.model.db.EqualizerBandEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomTypeConvertersTest {

    private val json = "[{\"gain\":10.0,\"frequency\":16000.0}]"
    private val data = listOf(
        EqualizerBandEntity(10f, 16000f)
    )

    @Test
    fun testFromString(){
        // when
        val actual = CustomTypeConverters.deserializeBands(json)

        // then
        assertEquals(data, actual)
    }

    @Test
    fun testFromList(){
        // when
        val actual = CustomTypeConverters.serializeBands(data)

        // then
        assertEquals(json, actual)
    }

}