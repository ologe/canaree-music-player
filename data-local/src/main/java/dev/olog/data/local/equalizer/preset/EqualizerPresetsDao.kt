package dev.olog.data.local.equalizer.preset

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EqualizerPresetsDao {

    @Query(
        """
        SELECT * FROM equalizer_preset
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
    abstract fun getPresetById(id: Long): EqualizerPresetEntity

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
    abstract suspend fun insertPresets(preset: List<EqualizerPresetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPreset(preset: EqualizerPresetEntity)

}