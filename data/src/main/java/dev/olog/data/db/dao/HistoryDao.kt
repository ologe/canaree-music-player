package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.entities.HistoryEntity
import dev.olog.data.db.entities.PodcastHistoryEntity
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.utils.assertBackgroundThread
import io.reactivex.Completable
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow

@Dao
internal abstract class HistoryDao {

    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun getAllTracksImpl(): List<HistoryEntity>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun getAllPodcastsImpl(): List<PodcastHistoryEntity>

    @Query("""
        SELECT * FROM song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun observeAllTracksImpl(): Flowable<List<HistoryEntity>>

    @Query("""
        SELECT * FROM podcast_song_history
        ORDER BY dateAdded
        DESC LIMIT 200
    """)
    internal abstract fun observeAllPodcastsImpl(): Flowable<List<PodcastHistoryEntity>>

    @Query("""DELETE FROM song_history""")
    abstract fun deleteAll()

    @Query("""DELETE FROM podcast_song_history""")
    abstract fun deleteAllPodcasts()

    @Query("""
        DELETE FROM song_history
        WHERE id = :songId
    """)
    abstract fun deleteSingle(songId: Long)

    @Query("""
        DELETE FROM podcast_song_history
        WHERE id = :podcastId
    """)
    abstract fun deleteSinglePodcast(podcastId: Long)

    fun getTracks(songGateway: SongGateway): List<Song> {
        assertBackgroundThread()
        val historyList = getAllTracksImpl()
        val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    fun getPodcasts(podcastGateway: PodcastGateway): List<Song> {
        assertBackgroundThread()
        val historyList = getAllPodcastsImpl()
        val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
        return historyList.mapNotNull { entity ->
            songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.id)
        }
    }

    fun observeTracks(songGateway: SongGateway): Flow<List<Song>> {
        return observeAllTracksImpl()
            .asFlow()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = songGateway.getAll().groupBy { it.id }
                historyList.mapNotNull { entity ->
                    songList[entity.songId]?.get(0)?.copy(idInPlaylist = entity.id)
                }
            }.assertBackground()
    }

    fun getAllPodcasts(podcastGateway: PodcastGateway): Flow<List<Song>> {
        return observeAllPodcastsImpl()
            .asFlow()
            .map { historyList ->
                val songList : Map<Long, List<Song>> = podcastGateway.getAll().groupBy { it.id }
                historyList.mapNotNull { entity ->
                    songList[entity.podcastId]?.get(0)?.copy(idInPlaylist = entity.id)
                }
            }.assertBackground()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertImpl(entity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    internal abstract fun insertPodcastImpl(entity: PodcastHistoryEntity)

    fun insert(id: Long): Completable {
        return Completable.fromCallable{ insertImpl(HistoryEntity(songId = id)) }
    }

    fun insertPodcasts(id: Long): Completable {
        return Completable.fromCallable{ insertPodcastImpl(PodcastHistoryEntity(podcastId = id)) }
    }

}
