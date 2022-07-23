package dev.olog.data.db.lyrics

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class OfflineLyricsDao {

    @Query("SELECT * FROM offline_lyrics WHERE trackId = :trackId")
     abstract fun observeLyrics(trackId: Long): Flow<List<OfflineLyricsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract suspend fun saveLyrics(lyrics: OfflineLyricsEntity)

}