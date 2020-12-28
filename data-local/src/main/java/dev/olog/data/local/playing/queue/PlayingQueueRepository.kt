package dev.olog.data.local.playing.queue

import dev.olog.core.entity.PlayingQueueTrack
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

internal class PlayingQueueRepository @Inject constructor(
    private val playingQueueDao: PlayingQueueDao,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    override suspend fun getAll(): List<PlayingQueueTrack> {
        try {
            val playingQueue = playingQueueDao.getAllAsSongs(
                songList = songGateway.getAll(),
                podcastList = podcastGateway.getAll()
            )
            if (playingQueue.isNotEmpty()) {
                return playingQueue
            }
            return songGateway.getAll().mapIndexed { index, track -> track.toPlayingQueueSong(index) }
        } catch (ex: SecurityException) {
            // sometimes this method is called without having storage permission
            Timber.e(ex)
            return emptyList()
        }
    }

    override fun observeAll(): Flow<List<PlayingQueueTrack>> {
        return playingQueueDao.observeAllAsSongs(songGateway, podcastGateway)
    }

    override suspend fun update(list: List<UpdatePlayingQueueUseCaseRequest>) {
        playingQueueDao.insert(list)
    }

    private fun Track.toPlayingQueueSong(progressive: Int): PlayingQueueTrack {
        return PlayingQueueTrack(
            track = this,
            serviceProgressive = progressive,
        )
    }

}
