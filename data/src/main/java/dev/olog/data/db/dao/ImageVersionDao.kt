package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.data.db.entities.ImageVersion

@Dao
abstract class ImageVersionDao {

    @Query(
        """
        SELECT * 
        FROM image_version
        WHERE hash = :hash
    """
    )
    abstract fun getVersion(hash: Int): ImageVersion?

    @Insert
    abstract fun insertVersion(version: ImageVersion)

    @Query(
        """
        UPDATE image_version
        SET version = version + 1
        WHERE hash = :hash
    """
    )
    abstract fun increaseVersion(hash: Int)

}