package dev.olog.core.entity

import org.junit.Assert.*
import org.junit.Test

class LastMetadataTest {

    @Test
    fun testNotEmptyTrue() {
        val item = LastMetadata("title", "", 1)
        assertTrue(item.isNotEmpty())
    }

    @Test
    fun testNotEmptyFalse() {
        val item = LastMetadata("", "", 1)
        assertFalse(item.isNotEmpty())
    }

    @Test
    fun testDescription() {
        val item = LastMetadata("title", "<unknown>", 1)
        assertEquals("title", item.description)
    }

    @Test
    fun testDescriptionNoSubtitle() {
        val item = LastMetadata("title", "subtitle", 1)
        assertEquals("title subtitle", item.description)
    }

}