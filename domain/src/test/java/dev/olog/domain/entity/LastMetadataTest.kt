package dev.olog.domain.entity

import org.junit.Assert.*
import org.junit.Test

class LastMetadataTest {

    @Test
    fun `should be not empty since title is non null`() {
        val item = LastMetadata("title", "", 1)
        assertTrue(item.isNotEmpty())
    }

    @Test
    fun `should be empty since title is null`() {
        val item = LastMetadata("", "", 1)
        assertFalse(item.isNotEmpty())
    }

    @Test
    fun `test description is empty when subtitle is unknown`() {
        val item = LastMetadata("title", "<unknown>", 1)
        assertEquals("title", item.description)
    }

    @Test
    fun `test description`() {
        val item = LastMetadata("title", "subtitle", 1)
        assertEquals("title subtitle", item.description)
    }

}