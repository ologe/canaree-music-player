package dev.olog.data.sort.mapper

import dev.olog.core.sort.GenericSort
import dev.olog.data.sort.SortTypeEntity
import org.junit.Assert
import org.junit.Test

class GenericSortMapperTest {

    @Test
    fun `test all toEntity`() {
        val map = mapOf(
            GenericSort.Title to SortTypeEntity.Title,
        )

        for (value in GenericSort.values()) {
            Assert.assertEquals(map[value], value.toEntity())
        }
    }

    @Test
    fun `test all toGenericSort`() {
        val map = mapOf(
            SortTypeEntity.Title to GenericSort.Title,
        )

        for (item in SortTypeEntity.values()) {
            val actual = try {
                item.toGenericSort()
            } catch (ex: NoSuchElementException) {
                null
            }
            Assert.assertEquals(map[item], actual)
        }
    }
    
}