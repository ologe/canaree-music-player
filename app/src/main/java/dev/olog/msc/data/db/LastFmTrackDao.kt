package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.data.entity.LastFmTrackImageEntity
import io.reactivex.Single

@Dao
abstract class LastFmTrackDao {

    @Query("SELECT * from last_fm_track_info where id = :id")
    internal abstract fun getInfoById(id: Long): Single<LastFmTrackEntity>

    @Query("SELECT * from last_fm_track_image where id = :id")
    internal abstract fun getImageById(id: Long): Single<LastFmTrackImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertInfo(entity: LastFmTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImage(entity: LastFmTrackImageEntity)

}