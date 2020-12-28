package dev.olog.data.local.playing.queue

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.core.entity.PlayingQueueTrack
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
internal abstract class PlayingQueueDao {

    @Query(
        """
        SELECT * FROM playing_queue
        ORDER BY internalId
    """
    )
    abstract suspend fun getAllImpl(): List<PlayingQueueEntity>

    @Query(
        """
        SELECT * FROM playing_queue
        ORDER BY internalId
    """
    )
    abstract fun observeAllImpl(): Flow<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue")
    abstract suspend fun deleteAllImpl()

    @Insert
    abstract suspend fun insertAllImpl(list: List<PlayingQueueEntity>)

    private fun makePlayingQueue(
        playingQueue: List<PlayingQueueEntity>,
        songList: List<Track>,
        podcastList: List<Track>
    ): List<PlayingQueueTrack> {
        // mapping to avoid O(n^2) iteration
        val mappedSongList = songList.groupBy { it.id }
        val mappedPodcastList = podcastList.groupBy { it.id }

        val result = mutableListOf<PlayingQueueTrack>()

        for (playingQueueEntity in playingQueue) {
            val id = playingQueueEntity.songId

            val fakeSongList = mappedSongList[id]
                ?: mappedPodcastList[id]
                ?: continue

            val track = fakeSongList[0] // only one track
            val playingQueueSong = track.toPlayingQueueSong(
                serviceProgressive = playingQueueEntity.serviceProgressive,
            )
            result.add(playingQueueSong)
        }
        return result
    }

    suspend fun getAllAsSongs(
        songList: List<Track>,
        podcastList: List<Track>
    ): List<PlayingQueueTrack> {
        val queueEntityList = getAllImpl()
        return makePlayingQueue(queueEntityList, songList, podcastList)
    }

    fun observeAllAsSongs(
        songGateway: SongGateway,
        podcastGateway: PodcastGateway
    ): Flow<List<PlayingQueueTrack>> {
        return this.observeAllImpl()
            .map {
                makePlayingQueue(it, songGateway.getAll(), podcastGateway.getAll())
            }
    }

    @Transaction
    open suspend fun insert(list: List<UpdatePlayingQueueUseCaseRequest>) {
        deleteAllImpl()
        val result = list.map {
            PlayingQueueEntity(
                songId = it.songId,
                serviceProgressive = it.serviceProgressive,
            )
        }
        insertAllImpl(result)
    }

    private fun Track.toPlayingQueueSong(
        serviceProgressive: Int
    ) : PlayingQueueTrack {
        return PlayingQueueTrack(
            track = this,
            serviceProgressive = serviceProgressive,
        )
    }


}