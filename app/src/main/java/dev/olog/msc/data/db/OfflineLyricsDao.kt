package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.OfflineLyricsEntity
import io.reactivex.Flowable

@Dao
abstract class OfflineLyricsDao {

    @Query("SELECT * FROM offline_lyrics WHERE trackId = :trackId")
    internal abstract fun observeLyrics(trackId: Long): Flowable<List<OfflineLyricsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun saveLyrics(lyrics: OfflineLyricsEntity)

}