package dev.olog.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.data.model.db.HistoryEntity
import dev.olog.data.model.db.PodcastHistoryEntity
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class HistoryDao {

    companion object {
        private const val HISTORY_LIMIT = 100
    }

    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract fun getAllTracksImpl(): List<HistoryEntity>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract fun getAllPodcastsImpl(): List<PodcastHistoryEntity>

    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract fun observeAllTracksImpl(): Flow<List<HistoryEntity>>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract fun observeAllPodcastsImpl(): Flow<List<PodcastHistoryEntity>>

    @Query("""DELETE FROM song_history""")
    abstract suspend fun deleteAll()

    @Query("""DELETE FROM podcast_song_history""")
    abstract suspend fun deleteAllPodcasts()

    @Query("""
        DELETE FROM song_history
        WHERE id = :songId
    """)
    abstract suspend fun deleteSingle(songId: Long)

    @Query("""
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """)
    abstract suspend fun deleteSinglePodcast(podcastId: Long)

    fun getTracks(trackGateway: TrackGateway): List<Song> {
        assertBackgroundThread()
        val historyList = getAllTracksImpl()
        val songList : Map<Long, List<Song>> = trackGateway.getAllTracks().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    fun getPodcasts(trackGateway: TrackGateway): List<Song> {
        assertBackgroundThread()
        val historyList = getAllPodcastsImpl()
        val songList : Map<Long, List<Song>> = trackGateway.getAllPodcasts().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    fun observeTracks(trackGateway: TrackGateway): Flow<List<Song>> {
        return observeAllTracksImpl()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = trackGateway.getAllTracks().groupBy { it.id }
                historyList.mapNotNull { entity ->
                    songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
                }
            }
    }

    fun observePodcasts(trackGateway: TrackGateway): Flow<List<Song>> {
        return observeAllPodcastsImpl()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = trackGateway.getAllPodcasts().groupBy { it.id }
                historyList.mapNotNull { entity ->
                    songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.id)
                }
            }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertImpl(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract suspend fun insertPodcastImpl(entity: PodcastHistoryEntity)

    suspend fun insert(id: Long) {
        insertImpl(HistoryEntity(songId = id))
    }

    suspend fun insertPodcasts(id: Long) {
        insertPodcastImpl(PodcastHistoryEntity(podcastId = id))
    }

}
