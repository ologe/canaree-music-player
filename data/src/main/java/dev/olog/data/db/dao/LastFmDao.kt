package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.LastFmAlbumEntity
import dev.olog.data.db.entities.LastFmArtistEntity
import dev.olog.data.db.entities.LastFmTrackEntity

private const val CACHE_TIME = "1 year"

@Dao
@Deprecated(message = "delete")
internal abstract class LastFmDao {

    // track

    @Query(
        """
        SELECT * FROM last_fm_track_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    abstract suspend fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track_v2 WHERE id = :trackId")
    abstract suspend fun deleteTrack(trackId: Long)

    // album

    @Query(
        """
        SELECT * FROM last_fm_album_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    abstract suspend fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album_v2 WHERE id = :albumId")
    abstract suspend fun deleteAlbum(albumId: Long)

    // artist

    @Query(
        """
        SELECT * FROM last_fm_artist_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """
    )
    abstract suspend fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist_v2 WHERE id = :artistId")
    abstract suspend fun deleteArtist(artistId: Long)
}