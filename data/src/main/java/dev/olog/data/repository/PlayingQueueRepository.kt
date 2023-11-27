package dev.olog.data.repository

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.data.db.dao.PlayingQueueDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    private val playingQueueDao: PlayingQueueDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    override fun getAll(): List<PlayingQueueSong> {
        try {
//            assertBackgroundThread()
            val playingQueue =
                playingQueueDao.getAllAsSongs(songGateway.getAll(), podcastGateway.getAll())
            if (playingQueue.isNotEmpty()) {
                return playingQueue
            }
            return songGateway.getAll().mapIndexed { index, song -> song.toPlayingQueueSong(index) }
        } catch (ex: SecurityException) {
            // sometimes this method is called without having storage permission
            ex.printStackTrace()
            return emptyList()
        }
    }

    override fun observeAll(): Flow<List<PlayingQueueSong>> {
        return playingQueueDao.observeAllAsSongs(songGateway, podcastGateway)
    }

    override fun update(list: List<UpdatePlayingQueueUseCaseRequest>) {
        playingQueueDao.insert(list)
    }

    private fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
        return PlayingQueueSong(
            this.copy(idInPlaylist = progressive),
            getMediaId()
        )
    }

}
