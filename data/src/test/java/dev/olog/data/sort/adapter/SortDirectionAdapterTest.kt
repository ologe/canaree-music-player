package dev.olog.data.sort.adapter

import dev.olog.core.entity.sort.SortDirection
import org.junit.Assert
import org.junit.Test

class SortDirectionAdapterTest {

    @Test
    fun `test encode`() {
        val map = mapOf(
            SortDirection.ASCENDING to "asc",
            SortDirection.DESCENDING to "desc",
        )
        for (item in SortDirection.values()) {
            Assert.assertEquals(map[item], SortDirectionAdapter.encode(item))
        }
    }

    @Test
    fun `test decode`() {
        for (item in SortDirection.values()) {
            Assert.assertEquals(item, SortDirectionAdapter.decode(item.serialized))
        }
    }

}