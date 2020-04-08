package dev.olog.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.lib.model.db.LastFmAlbumEntity
import dev.olog.lib.model.db.LastFmArtistEntity
import dev.olog.lib.model.db.LastFmTrackEntity

@Dao
internal interface LastFmDao {

    companion object {
        private const val CACHE_TIME = "1 month"
    }

    // track

    @Query(
        """
        SELECT * FROM last_fm_track_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track_v2 WHERE id = :trackId")
    fun deleteTrack(trackId: Long)

    // album

    @Query(
        """
        SELECT * FROM last_fm_album_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album_v2 WHERE id = :albumId")
    fun deleteAlbum(albumId: Long)

    // artist

    @Query(
        """
        SELECT * FROM last_fm_artist_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist_v2 WHERE id = :artistId")
    fun deleteArtist(artistId: Long)
}