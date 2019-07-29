package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.data.db.entities.ImageVersionEntity

@Dao
abstract class ImageVersionDao {

    @Query(
        """
        SELECT * 
        FROM image_version
        WHERE mediaId = :mediaId
    """
    )
    abstract fun getVersion(mediaId: String): ImageVersionEntity?

    @Insert
    abstract fun insertVersion(version: ImageVersionEntity)

    @Query(
        """
        UPDATE image_version
        SET version = version + 1
        WHERE mediaId = :mediaId
    """
    )
    abstract fun increaseVersion(mediaId: String)

}