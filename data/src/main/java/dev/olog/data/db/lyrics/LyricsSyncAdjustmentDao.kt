package dev.olog.data.db.lyrics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LyricsSyncAdjustmentDao {

    @Query(
        """
        SELECT * 
        FROM lyrics_sync_adjustment
        WHERE id = :id
    """
    )
    abstract fun getSync(id: Long): LyricsSyncAdjustmentEntity?

    @Query(
        """
        SELECT * 
        FROM lyrics_sync_adjustment
        WHERE id = :id
    """
    )
    abstract fun observeSync(id: Long): Flow<LyricsSyncAdjustmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun setSync(entity: LyricsSyncAdjustmentEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSyncIfEmpty(entity: LyricsSyncAdjustmentEntity)

}