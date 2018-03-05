package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmArtistEntity
import dev.olog.msc.data.entity.LastFmTrackEntity
import io.reactivex.Single

@Dao
abstract class LastFmDao {

    // track

    @Query("SELECT * from last_fm_track where id = :id")
    internal abstract fun getTrack(id: Long): Single<LastFmTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertTrack(entity: LastFmTrackEntity): Long

    // album

    @Query("SELECT * from last_fm_album where id = :id")
    internal abstract fun getAlbum(id: Long): Single<LastFmAlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertAlbum(entity: LastFmAlbumEntity): Long

    // artist

    @Query("SELECT * from last_fm_artist where id = :id")
    internal abstract fun getArtist(id: Long): Single<LastFmArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertArtist(entity: LastFmArtistEntity): Long

    // used images

    @Query("SELECT * from used_image")
    internal abstract fun getAllUsedImages(): List<UsedImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertUsedTrackImage(entity: UsedImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertUsedAlbumImage(entity: UsedImageEntity)

    @Query("DELETE FROM used_image WHERE id = :trackId AND isAlbum")
    internal abstract fun deleteUsedTrackImage(trackId: Long)

    @Query("DELETE FROM used_image WHERE id = :albumId AND NOT isAlbum")
    internal abstract fun deleteUsedAlbumImage(albumId: Long)

}