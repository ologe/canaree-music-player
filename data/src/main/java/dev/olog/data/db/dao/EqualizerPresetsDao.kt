package dev.olog.data.db.dao

import androidx.room.*
import dev.olog.data.db.entities.EqualizerPresetEntity
import io.reactivex.Flowable

@Dao
internal abstract class EqualizerPresetsDao {

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
    abstract fun observePresetById(id: Long): Flowable<EqualizerPresetEntity>

    @Delete
    abstract suspend fun deletePreset(preset: EqualizerPresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPresets(preset: List<EqualizerPresetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPreset(preset: EqualizerPresetEntity)

}