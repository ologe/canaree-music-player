package dev.olog.msc.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import dev.olog.msc.data.entity.*

private const val ARTIST_CACHE_TIME = "1 months"
private const val ALBUM_CACHE_TIME = "2 months"

@Dao
abstract class LastFmDao {

    // track

    @Query("""
        SELECT * FROM last_fm_track
        WHERE id = :id
        AND added BETWEEN date('now', '-$ALBUM_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getTrack(id: Long): LastFmTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertTrack(entity: LastFmTrackEntity): Long

    @Query("DELETE FROM last_fm_track WHERE id = :trackId")
    internal abstract fun deleteTrack(trackId: Long)

    // album

    @Query("""
        SELECT * FROM last_fm_album
        WHERE id = :id
        AND added BETWEEN date('now', '-$ALBUM_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getAlbum(id: Long): LastFmAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertAlbum(entity: LastFmAlbumEntity): Long

    @Query("DELETE FROM last_fm_album WHERE id = :albumId")
    internal abstract fun deleteAlbum(albumId: Long)

    // artist

    @Query("""
        SELECT * FROM last_fm_artist
        WHERE id = :id
        AND added BETWEEN date('now', '-$ARTIST_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getArtist(id: Long): LastFmArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertArtist(entity: LastFmArtistEntity): Long

    @Query("DELETE FROM last_fm_artist WHERE id = :artistId")
    internal abstract fun deleteArtist(artistId: Long)

    // podcast

    @Query("""
        SELECT * FROM last_fm_podcast
        WHERE id = :id
        AND added BETWEEN date('now', '-$ALBUM_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getPodcast(id: Long): LastFmPodcastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcast(entity: LastFmPodcastEntity): Long

    @Query("DELETE FROM last_fm_podcast WHERE id = :podcastId")
    internal abstract fun deletePodcast(podcastId: Long)

    // podcast album

    @Query("""
        SELECT * FROM last_fm_podcast_album
        WHERE id = :id
        AND added BETWEEN date('now', '-$ALBUM_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getPodcastAlbum(id: Long): LastFmPodcastAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcastAlbum(entity: LastFmPodcastAlbumEntity): Long

    @Query("DELETE FROM last_fm_podcast_album WHERE id = :albumId")
    internal abstract fun deletePodcastAlbum(albumId: Long)


    // podcast artist

    @Query("""
        SELECT * FROM last_fm_podcast_artist
        WHERE id = :id
        AND added BETWEEN date('now', '-$ARTIST_CACHE_TIME') AND date('now')
    """)
    internal abstract fun getPodcastArtist(id: Long): LastFmPodcastArtistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcastArtist(entity: LastFmPodcastArtistEntity): Long

    @Query("DELETE FROM last_fm_podcast_artist WHERE id = :artistId")
    internal abstract fun deletePodcastArtist(artistId: Long)
}