package dev.olog.data.sort

import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.sort.SongSortType
import dev.olog.data.sort.db.SortDao
import dev.olog.data.sort.db.SortDirectionEntity
import dev.olog.data.sort.db.SortEntity
import dev.olog.data.sort.db.SortEntityTable
import dev.olog.data.sort.db.SortTypeEntity
import javax.inject.Inject

class SortRepository @Inject constructor(
    private val dao: SortDao,
) {

    fun getAllSongsSort(): AllSongsSort {
        val model = dao.getSort(SortEntityTable.Songs)
        return AllSongsSort(
            type = SongSortType(model.columnName.toDomain()),
            direction = model.direction.toDomain()
        )
    }

    fun setAllSongsSort(model: AllSongsSort) {
        val entity = SortEntity(
            tableName = SortEntityTable.Songs,
            columnName = SortTypeEntity(model.type.type),
            direction = SortDirectionEntity(model.direction),
        )
        dao.setSort(entity)
    }

}