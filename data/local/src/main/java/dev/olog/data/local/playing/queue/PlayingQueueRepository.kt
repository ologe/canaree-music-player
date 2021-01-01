package dev.olog.data.local.playing.queue

import dev.olog.domain.entity.PlayingQueueTrack
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.interactor.UpdatePlayingQueueUseCase
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

    override suspend fun update(list: List<UpdatePlayingQueueUseCase.Request>) {
        playingQueueDao.insert(list)
    }

    private fun Track.toPlayingQueueSong(progressive: Int): PlayingQueueTrack {
        return PlayingQueueTrack(
            track = this,
            serviceProgressive = progressive,
        )
    }

}
