package dev.olog.msc.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.msc.data.entity.OfflineLyricsEntity
import io.reactivex.Flowable

@Dao
abstract class OfflineLyricsDao {

    @Query("SELECT * FROM offline_lyrics WHERE trackId = :trackId")
    internal abstract fun observeLyrics(trackId: Long): Flowable<List<OfflineLyricsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun saveLyrics(lyrics: OfflineLyricsEntity)

}