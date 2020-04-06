package dev.olog.domain.entity

import org.junit.Assert.assertEquals
import org.junit.Test

class EqualizerBandTest {

    @Test
    fun testLowFrequency() {
        val band = EqualizerBand(10f, 800f)
        assertEquals("800", band.displayableFrequency)
    }

    @Test
    fun test1000Frequency() {
        val band = EqualizerBand(10f, 1000f)
        assertEquals("1K", band.displayableFrequency)
    }

    @Test
    fun testHighFrequency() {
        val band = EqualizerBand(10f, 16000f)
        assertEquals("16K", band.displayableFrequency)
    }

}