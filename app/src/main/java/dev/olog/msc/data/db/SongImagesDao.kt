package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.SongImageEntity

@Dao
abstract class SongImagesDao {

    @Query("select * from song_image")
    abstract fun getAll() : List<SongImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: SongImageEntity)

    @Query("delete from song_image where id = :id")
    abstract fun delete(id: Long)

}