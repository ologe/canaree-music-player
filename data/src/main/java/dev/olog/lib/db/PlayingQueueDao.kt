package dev.olog.lib.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.UpdatePlayingQueueUseCase
import dev.olog.lib.model.db.PlayingQueueEntity
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * TODO support spotify preview url
 */
@Dao
internal abstract class PlayingQueueDao {

    @Query(
        """
        SELECT * FROM playing_queue_2
        ORDER BY progressive
    """
    )
    abstract fun getAllImpl(): List<PlayingQueueEntity>

    @Query(
        """
        SELECT * FROM playing_queue_2
        ORDER BY progressive
    """
    )
    abstract fun observeAllImpl(): Flow<List<PlayingQueueEntity>>

    @Query("DELETE FROM playing_queue_2")
    abstract fun deleteAllImpl()

    @Insert
    abstract fun insertAllImpl(list: List<PlayingQueueEntity>)

    private fun makePlayingQueue(
        playingQueue: List<PlayingQueueEntity>,
        songList: List<Song>,
        podcastList: List<Song>
    ): List<PlayingQueueSong> {
        // mapping to avoid O(n^2) iteration
        val mappedSongList = songList.groupBy { it.id }
        val mappedPodcastList = podcastList.groupBy { it.id }

        val result = mutableListOf<PlayingQueueSong>()

        for (playingQueueEntity in playingQueue) {
            val id = playingQueueEntity.songId

            val fakeSongList = mappedSongList[id]
                ?: mappedPodcastList[id]
                ?: continue

            val song = fakeSongList[0] // only one song
            val playingQueueSong = song.toPlayingQueueSong(
                playingQueueEntity.idInPlaylist,
                playingQueueEntity.category,
                playingQueueEntity.categoryValue
            )
            result.add(playingQueueSong)
        }
        return result
    }

    fun getAllAsSongs(songList: List<Song>, podcastList: List<Song>): List<PlayingQueueSong> {
        val queueEntityList = getAllImpl()
        return makePlayingQueue(queueEntityList, songList, podcastList)
    }

    fun observeAllAsSongs(trackGateway: TrackGateway): Flow<List<PlayingQueueSong>> {
        return this.observeAllImpl()
            .map {
                makePlayingQueue(it, trackGateway.getAllTracks(), trackGateway.getAllPodcasts())
            }
    }

    @Transaction
    open fun insert(list: List<UpdatePlayingQueueUseCase.Request>) {
        assertBackgroundThread()

        deleteAllImpl()
        val result = list.map {
            PlayingQueueEntity(
                songId = it.songId,
                category = it.mediaId.category.toString(),
                categoryValue = it.mediaId.categoryId,
                idInPlaylist = it.idInPlaylist
            )
        }
        insertAllImpl(result)
    }

    private fun Song.toPlayingQueueSong(
        idInPlaylist: Int,
        category: String,
        categoryValue: String
    ): PlayingQueueSong {

        val parentMediaId = MediaId.Category(MediaIdCategory.valueOf(category), categoryValue)

        return PlayingQueueSong(
            this.copy(idInPlaylist = idInPlaylist),
            parentMediaId.playableItem("${this.id}")
        )
    }


}