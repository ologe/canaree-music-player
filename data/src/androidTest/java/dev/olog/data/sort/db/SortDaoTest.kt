package dev.olog.data.sort.db

import dev.olog.data.DatabaseTest
import org.junit.Assert
import org.junit.Test

class SortDaoTest : DatabaseTest() {

    private val sut = db.sortDao()

    @Test
    fun test() {
        // can't check for initial state to be null (not set) because default sorting
        // is defined on migration with no nullable getters
        val sort = SortEntity(SortEntityTable.Albums, SortTypeEntity.Artist, SortDirectionEntity.Descending)
        sut.setSort(sort)

        Assert.assertEquals(sort, sut.getSort(SortEntityTable.Albums))
    }

}