package dev.olog.core.entity.sort

import org.junit.Assert.assertEquals
import org.junit.Test

class SortArrangingTest {

    @Test
    fun testAscending() {
        assertEquals(
            "ASC",
            SortArranging.ASCENDING
        )
    }

    @Test
    fun testDescending() {
        assertEquals(
            "DESC",
            SortArranging.DESCENDING
        )
    }

}