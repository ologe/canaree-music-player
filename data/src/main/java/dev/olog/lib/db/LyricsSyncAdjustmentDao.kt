package dev.olog.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.lib.model.db.LyricsSyncAdjustmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LyricsSyncAdjustmentDao {

    @Query(
        """
        SELECT * 
        FROM lyrics_sync_adjustment
        WHERE id = :id
    """
    )
    fun getSync(id: Long): LyricsSyncAdjustmentEntity?

    @Query(
        """
        SELECT * 
        FROM lyrics_sync_adjustment
        WHERE id = :id
    """
    )
    fun observeSync(id: Long): Flow<LyricsSyncAdjustmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setSync(entity: LyricsSyncAdjustmentEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSyncIfEmpty(entity: LyricsSyncAdjustmentEntity)

}