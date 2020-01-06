package dev.olog.data.db

import androidx.room.*
import dev.olog.data.model.db.EqualizerPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class EqualizerPresetsDao {

    @Query(
        """
        SELECT * FROM equalizer_preset
        ORDER BY id
    """
    )
    abstract fun getPresets(): List<EqualizerPresetEntity>

    @Query(
        """
        SELECT * 
        FROM equalizer_preset
        WHERE id = :id
    """
    )
    abstract fun getPresetById(id: Long): EqualizerPresetEntity?

    @Query(
        """
        SELECT * 
        FROM equalizer_preset
        WHERE id = :id
    """
    )
    abstract fun observePresetById(id: Long): Flow<EqualizerPresetEntity>

    @Delete
    abstract suspend fun deletePreset(preset: EqualizerPresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPresets(vararg preset: EqualizerPresetEntity)

}