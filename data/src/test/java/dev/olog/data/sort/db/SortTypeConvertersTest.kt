package dev.olog.data.sort.db

import org.junit.Assert
import org.junit.Test

class SortTypeConvertersTest {

    private val sut = SortTypeConverters()

    @Test
    fun `test convert sort table`() {
        val value = SortEntityTable.Songs
        Assert.assertEquals(
            value,
            sut.deserializeSortTable(sut.serializeSortTable(value))
        )
    }

    @Test
    fun `test convert sort direction`() {
        val value = SortDirectionEntity.Descending
        Assert.assertEquals(
            value,
            sut.deserializeSortDirection(sut.serializeSortDirection(value))
        )
    }

    @Test
    fun `test convert sort type`() {
        val value = SortTypeEntity.Date
        Assert.assertEquals(
            value,
            sut.deserializeSortType(sut.serializeSortType(value))
        )
    }

}