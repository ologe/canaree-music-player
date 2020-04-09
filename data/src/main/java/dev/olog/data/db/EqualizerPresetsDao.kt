package dev.olog.data.db

import androidx.room.*
import dev.olog.data.model.db.EqualizerPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface EqualizerPresetsDao {

    @Query(
        """
        SELECT * FROM equalizer_preset
        ORDER BY id
    """
    )
    fun getPresets(): List<EqualizerPresetEntity>

    @Query(
        """
        SELECT * 
        FROM equalizer_preset
        WHERE id = :id
    """
    )
    fun getPresetById(id: Long): EqualizerPresetEntity?

    @Query(
        """
        SELECT * 
        FROM equalizer_preset
        WHERE id = :id
    """
    )
    fun observePresetById(id: Long): Flow<EqualizerPresetEntity>

    @Delete
    suspend fun deletePreset(preset: EqualizerPresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(vararg preset: EqualizerPresetEntity)

}