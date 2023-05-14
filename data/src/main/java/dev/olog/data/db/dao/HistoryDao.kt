package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.HistoryEntity
import dev.olog.data.db.entities.PodcastHistoryEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    companion object {
        private const val HISTORY_LIMIT = 100
    }

    @Query("""
        SELECT mediastore_audio.* 
        FROM song_history JOIN mediastore_audio 
            ON song_history.songId = mediastore_audio._id  
        ORDER BY song_history.dateAdded DESC 
        LIMIT $HISTORY_LIMIT
    """)
    fun getAllTracks(): List<MediaStoreAudioEntity>

    @Query("""
        SELECT mediastore_audio.* 
        FROM podcast_song_history JOIN mediastore_audio 
            ON podcast_song_history.podcastId = mediastore_audio._id  
        ORDER BY podcast_song_history.dateAdded DESC
        LIMIT $HISTORY_LIMIT
    """)
    fun getAllPodcasts(): List<MediaStoreAudioEntity>

    @Query("""
        SELECT mediastore_audio.* 
        FROM song_history JOIN mediastore_audio 
            ON song_history.songId = mediastore_audio._id  
        ORDER BY song_history.dateAdded DESC
        LIMIT $HISTORY_LIMIT
    """)
    fun observeAllTracks(): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT mediastore_audio.* 
        FROM podcast_song_history JOIN mediastore_audio 
            ON podcast_song_history.podcastId = mediastore_audio._id  
        ORDER BY podcast_song_history.dateAdded DESC
        LIMIT $HISTORY_LIMIT
    """)
    fun observeAllPodcasts(): Flow<List<MediaStoreAudioEntity>>

    @Query("DELETE FROM song_history")
    suspend fun deleteAllSongs()

    @Query("DELETE FROM podcast_song_history")
    suspend fun deleteAllPodcasts()

    @Query("""
        DELETE FROM song_history
        WHERE id = :songId
    """)
    suspend fun deleteSong(songId: Long)

    @Query("""
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """)
    suspend fun deletePodcast(podcastId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcast(entity: PodcastHistoryEntity)

}
