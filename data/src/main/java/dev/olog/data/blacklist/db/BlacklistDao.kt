package dev.olog.data.blacklist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class BlacklistDao {

    @Query("SELECT * FROM blacklist")
    abstract suspend fun getAll(): List<BlacklistEntity>

    @Query("DELETE FROM blacklist")
    abstract suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(items: List<BlacklistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(vararg items: BlacklistEntity)

    @Transaction
    open suspend fun replaceAll(items: List<BlacklistEntity>) {
        deleteAll()
        insertAll(items)
    }

}