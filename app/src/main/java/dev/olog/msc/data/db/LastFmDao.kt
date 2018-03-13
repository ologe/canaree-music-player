package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmArtistEntity
import dev.olog.msc.data.entity.LastFmTrackEntity

private const val CACHE_TIME = "1 months"

@Dao
abstract class LastFmDao {

    // track

    @Query("SELECT * FROM last_fm_track " +
            "WHERE id = :id AND title = :title AND artist = :artist AND album = :album " +
            "AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')")
    internal abstract fun getTrack(id: Long, title: String, artist: String, album: String): LastFmTrackEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertTrack(entity: LastFmTrackEntity): Long

    // album

    @Query("SELECT * FROM last_fm_album " +
            "WHERE id = :id AND title = :title AND artist = :artist " +
            "AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')")
    internal abstract fun getAlbum(id: Long, title: String, artist: String): LastFmAlbumEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertAlbum(entity: LastFmAlbumEntity): Long

    // artist

    @Query("SELECT * FROM last_fm_artist")
    internal abstract fun getAllArtists(): List<LastFmArtistEntity>

    @Query("SELECT * FROM last_fm_artist " +
            "WHERE id = :id " +
            "AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')")
    internal abstract fun getArtist(id: Long): LastFmArtistEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertArtist(entity: LastFmArtistEntity): Long

    // used images, used by album and tracks

    @Query("SELECT * FROM used_image")
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