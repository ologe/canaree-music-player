package dev.olog.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.lib.model.db.OfflineLyricsEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface OfflineLyricsDao {

    @Query("SELECT * FROM offline_lyrics WHERE trackId = :trackId")
    fun observeLyrics(trackId: Long): Flow<List<OfflineLyricsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLyrics(lyrics: OfflineLyricsEntity)

}