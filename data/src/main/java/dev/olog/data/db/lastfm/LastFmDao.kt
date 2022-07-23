package dev.olog.data.db.lastfm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal abstract class LastFmDao {

    companion object {
        private const val CACHE_TIME = "1 month"
    }

    // track

    @Query("""
        SELECT * FROM last_fm_track_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track_v2 WHERE id = :trackId")
     abstract fun deleteTrack(trackId: Long)

    // album

    @Query("""
        SELECT * FROM last_fm_album_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album_v2 WHERE id = :albumId")
     abstract fun deleteAlbum(albumId: Long)

    // artist

    @Query("""
        SELECT * FROM last_fm_artist_v2
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist_v2 WHERE id = :artistId")
     abstract fun deleteArtist(artistId: Long)
}