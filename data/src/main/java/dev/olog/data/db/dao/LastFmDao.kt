package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.*

private const val CACHE_TIME = "7 days"

@Dao
abstract class LastFmDao {

    // track

    @Query("""
        SELECT * FROM last_fm_track
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track WHERE id = :trackId")
     abstract fun deleteTrack(trackId: Long)

    // album

    @Query("""
        SELECT * FROM last_fm_album
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album WHERE id = :albumId")
     abstract fun deleteAlbum(albumId: Long)

    // artist

    @Query("""
        SELECT * FROM last_fm_artist
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist WHERE id = :artistId")
     abstract fun deleteArtist(artistId: Long)

    // podcast

    @Query("""
        SELECT * FROM last_fm_podcast
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getPodcast(id: Long): LastFmPodcastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertPodcast(entity: LastFmPodcastEntity): Long

    @Query("DELETE FROM last_fm_podcast WHERE id = :podcastId")
     abstract fun deletePodcast(podcastId: Long)

    // podcast album

    @Query("""
        SELECT * FROM last_fm_podcast_album
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getPodcastAlbum(id: Long): LastFmPodcastAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertPodcastAlbum(entity: LastFmPodcastAlbumEntity): Long

    @Query("DELETE FROM last_fm_podcast_album WHERE id = :albumId")
     abstract fun deletePodcastAlbum(albumId: Long)


    // podcast artist

    @Query("""
        SELECT * FROM last_fm_podcast_artist
        WHERE id = :id
        AND added BETWEEN date('now', '-$CACHE_TIME') AND date('now')
    """)
     abstract fun getPodcastArtist(id: Long): LastFmPodcastArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     abstract fun insertPodcastArtist(entity: LastFmPodcastArtistEntity): Long

    @Query("DELETE FROM last_fm_podcast_artist WHERE id = :artistId")
     abstract fun deletePodcastArtist(artistId: Long)
}