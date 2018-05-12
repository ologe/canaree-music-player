package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.UsedAlbumImageEntity
import dev.olog.msc.data.entity.UsedArtistImageEntity
import dev.olog.msc.data.entity.UsedTrackImageEntity

@Dao
abstract class UsedImageDao {

    // get all

    @Query("""
        SELECT *
        FROM used_image_track
        ORDER BY id
        """)
    internal abstract fun getAllImagesForTracks(): List<UsedTrackImageEntity>

    @Query("""
        SELECT *
        FROM used_image_album
        ORDER BY id
        """)
    internal abstract fun getAllImagesForAlbums(): List<UsedAlbumImageEntity>

    @Query("""
        SELECT *
        FROM used_image_artist
        ORDER BY id
        """)
    internal abstract fun getAllImagesForArtists(): List<UsedArtistImageEntity>

    // get by param

    @Query("SELECT image FROM used_image_track WHERE id = :id")
    internal abstract fun getImageForTrack(id: Long): String?

    @Query("SELECT image FROM used_image_album WHERE id = :id")
    internal abstract fun getImageForAlbum(id: Long): String?

    @Query("SELECT image FROM used_image_artist WHERE id = :id")
    internal abstract fun getImageForArtist(id: Long): String?

    // insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertForTrack(entity: UsedTrackImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertForAlbum(entity: UsedAlbumImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertForArtist(entity: UsedArtistImageEntity)

    // delete

    @Query("DELETE FROM used_image_track WHERE id = :id")
    internal abstract fun deleteForTrack(id: Long)

    @Query("DELETE FROM used_image_album WHERE id = :id")
    internal abstract fun deleteForAlbum(id: Long)

    @Query("DELETE FROM used_image_artist WHERE id = :id")
    internal abstract fun deleteForArtist(id: Long)

}