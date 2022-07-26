package dev.olog.data.sort.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class SortDao {

    @Query("SELECT * FROM sort WHERE tableName = :tableName")
    // todo made suspend
    abstract fun getSort(tableName: SortEntityTable): SortEntity

    @Insert
    // todo made suspend
    abstract fun setSort(model: SortEntity)

}