package dev.olog.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.data.entity.ImageFolderEntity

@Dao
abstract class FolderImagesDao {

    @Query("SELECT * FROM folder_images WHERE key = :folderPath")
    abstract fun getByParam(folderPath: String): ImageFolderEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(image: ImageFolderEntity)

}