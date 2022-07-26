package dev.olog.data.sort.db

import dev.olog.core.entity.sort.SortDirection
import org.junit.Assert
import org.junit.Test

class SortDirectionEntityTest {

    @Test
    fun `test seriaized values`() {
        Assert.assertEquals("asc", SortDirectionEntity.Ascending.serializedValue)
        Assert.assertEquals("asc", SortDirectionEntity.Ascending.toString())
        Assert.assertEquals("asc", SORT_DIRECTION_ASC)

        Assert.assertEquals("desc", SortDirectionEntity.Descending.serializedValue)
        Assert.assertEquals("desc", SortDirectionEntity.Descending.toString())
        Assert.assertEquals("desc", SORT_DIRECTION_DESC)
    }

    @Test
    fun `test mappers`() {
        Assert.assertEquals(SortDirectionEntity.Ascending, SortDirectionEntity(SortDirection.ASCENDING))
        Assert.assertEquals(SortDirectionEntity.Descending, SortDirectionEntity(SortDirection.DESCENDING))

        Assert.assertEquals(SortDirection.ASCENDING, SortDirectionEntity.Ascending.toDomain())
        Assert.assertEquals(SortDirection.DESCENDING, SortDirectionEntity.Descending.toDomain())
    }

}