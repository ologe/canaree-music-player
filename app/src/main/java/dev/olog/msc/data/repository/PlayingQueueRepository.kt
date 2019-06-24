package dev.olog.msc.data.repository

import dev.olog.data.db.dao.AppDatabase
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.msc.domain.gateway.PodcastGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.core.MediaId
import dev.olog.core.gateway.SongGateway2
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class PlayingQueueRepository @Inject constructor(
    database: AppDatabase,
    private val songGateway: SongGateway2,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(
                        songGateway.observeAll().asObservable().firstOrError(),
                        podcastGateway.getAll().firstOrError()
                ).firstOrError(),

                songGateway.observeAll().asObservable().firstOrError()
                        .map { it.mapIndexed { index, song -> song.toPlayingQueueSong(index) } }
        ).filter { it.isNotEmpty() }.firstOrError()
    }

    override fun observeAll(): Observable<List<PlayingQueueSong>> {
        return playingQueueDao.getAllAsSongs(
                songGateway.observeAll().asObservable().firstOrError(),
                podcastGateway.getAll().firstOrError()
        )
    }

    override fun update(list: List<UpdatePlayingQueueUseCaseRequest>): Completable {
        return playingQueueDao.insert(list)
    }

    override fun observeMiniQueue(): Observable<List<PlayingQueueSong>> {
        return playingQueueDao.observeMiniQueue(
                songGateway.observeAll().asObservable().firstOrError(),
                podcastGateway.getAll().firstOrError()
        )
    }

    override fun updateMiniQueue(tracksId: List<Pair<Int, Long>>) {
        playingQueueDao.updateMiniQueue(tracksId)
    }

    private fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
        return PlayingQueueSong(
            this.id,
            progressive,
            MediaId.songId(this.id),
            this.artistId,
            this.albumId,
            this.title,
            this.artist,
            this.albumArtist,
            this.album,
            this.duration,
            this.dateAdded,
            this.path,
            this.folder,
            this.discNumber,
            this.trackNumber,
            false
        )
    }

}
