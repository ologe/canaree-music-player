package dev.olog.data.spotify.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.spotify.entity.SpotifyTrackAudioFeatureEntity
import dev.olog.data.spotify.entity.SpotifyTrackEntity

@Dao
abstract class SpotifyTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMultipleTrack(list: List<SpotifyTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMultipleTrackAudioFeature(list: List<SpotifyTrackAudioFeatureEntity>)

    @Query("""
        SELECT * 
        FROM spotify_tracks
    """)
    abstract fun getTracks(): List<SpotifyTrackEntity>

    @Query("""
        SELECT * 
        FROM spotify_tracks
        WHERE spotifyId != ''
    """)
    abstract fun getValidTracks(): List<SpotifyTrackEntity>

    @Query("""
        SELECT * 
        FROM spotify_tracks_audio_feature
    """)
    abstract fun getTracksAudioFeature(): List<SpotifyTrackAudioFeatureEntity>

}