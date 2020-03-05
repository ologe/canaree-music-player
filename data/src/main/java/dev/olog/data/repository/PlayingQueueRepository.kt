package dev.olog.data.repository

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCase
import dev.olog.data.db.PlayingQueueDao
import dev.olog.data.mapper.toPlayingQueueSong
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    private val playingQueueDao: PlayingQueueDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    override fun getAll(): List<PlayingQueueSong> {
        try {
            val playingQueue =
                playingQueueDao.getAllAsSongs(songGateway.getAll(), podcastGateway.getAll())
            if (playingQueue.isNotEmpty()) {
                return playingQueue
            }
            return songGateway.getAll().mapIndexed { index, song -> song.toPlayingQueueSong(index) }
        } catch (ex: SecurityException) {
            // sometimes this method is called without having storage permission
            Timber.e(ex)
            return emptyList()
        }
    }

    override fun observeAll(): Flow<List<PlayingQueueSong>> {
        return playingQueueDao.observeAllAsSongs(songGateway, podcastGateway)
            .assertBackground()
    }

    override fun update(list: List<UpdatePlayingQueueUseCase.Request>) {
        assertBackgroundThread()
        playingQueueDao.insert(list)
    }

}
