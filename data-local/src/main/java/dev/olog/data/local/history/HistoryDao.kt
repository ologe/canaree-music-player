package dev.olog.data.local.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
abstract class HistoryDao {

    companion object {
        private const val HISTORY_LIMIT = 100
    }

    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract suspend fun getAllTracksImpl(): List<HistoryEntity>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT $HISTORY_LIMIT
    """)
    internal abstract suspend fun getAllPodcastsImpl(): List<PodcastHistoryEntity>

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

    suspend fun getTracks(songGateway: SongGateway): List<Song> {
        val historyList = getAllTracksImpl()
        val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    suspend fun getPodcasts(podcastGateway: PodcastGateway): List<Song> {
        val historyList = getAllPodcastsImpl()
        val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    fun observeTracks(songGateway: SongGateway): Flow<List<Song>> {
        return observeAllTracksImpl()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                historyList.mapNotNull { entity ->
                    songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
                }
            }
    }

    fun observePodcasts(podcastGateway: PodcastGateway): Flow<List<Song>> {
        return observeAllPodcastsImpl()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
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
