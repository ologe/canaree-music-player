package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.UsedAlbumImageEntity
import dev.olog.data.db.entities.UsedArtistImageEntity
import dev.olog.data.db.entities.UsedTrackImageEntity

@Dao
internal abstract class UsedImageDao {

    // get all

    @Query("""
        SELECT *
        FROM used_image_track
        ORDER BY id
        """)
     abstract fun getAllImagesForTracks(): List<UsedTrackImageEntity>

    @Query("""
        SELECT *
        FROM used_image_album
        ORDER BY id
        """)
     abstract fun getAllImagesForAlbums(): List<UsedAlbumImageEntity>

    @Query("""
        SELECT *
        FROM used_image_artist
        ORDER BY id
        """)
     abstract fun getAllImagesForArtists(): List<UsedArtistImageEntity>

    // get by param

    @Query("SELECT image FROM used_image_track WHERE id = :id")
     abstract fun getImageForTrack(id: Long): String?

    @Query("SELECT image FROM used_image_album WHERE id = :id")
     abstract fun getImageForAlbum(id: Long): String?

    @Query("SELECT image FROM used_image_artist WHERE id = :id")
     abstract fun getImageForArtist(id: Long): String?

    // insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertForTrack(entity: UsedTrackImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertForAlbum(entity: UsedAlbumImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertForArtist(entity: UsedArtistImageEntity)

    // delete

    @Query("DELETE FROM used_image_track WHERE id = :id")
     abstract fun deleteForTrack(id: Long)

    @Query("DELETE FROM used_image_album WHERE id = :id")
     abstract fun deleteForAlbum(id: Long)

    @Query("DELETE FROM used_image_artist WHERE id = :id")
     abstract fun deleteForArtist(id: Long)

}