package dev.olog.data.repository

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.core.entity.track.getMediaId
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.gateway.PodcastGateway
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import dev.olog.data.db.dao.AppDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class PlayingQueueRepository @Inject constructor(
    database: AppDatabase,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(
                        songGateway.observeAll().asObservable().firstOrError(),
                        podcastGateway.observeAll().asObservable().firstOrError()
                ).firstOrError(),

                songGateway.observeAll().asObservable().firstOrError()
                        .map { it.mapIndexed { index, song -> song.toPlayingQueueSong(index) } }
        ).filter { it.isNotEmpty() }.firstOrError()
    }

    override fun observeAll(): Observable<List<PlayingQueueSong>> {
        return playingQueueDao.getAllAsSongs(
                songGateway.observeAll().asObservable().firstOrError(),
                podcastGateway.observeAll().asObservable().firstOrError()
        )
    }

    override fun update(list: List<UpdatePlayingQueueUseCaseRequest>): Completable {
        return playingQueueDao.insert(list)
    }

    private fun Song.toPlayingQueueSong(progressive: Int): PlayingQueueSong {
        return PlayingQueueSong(
            this.id,
            progressive,
            getMediaId(),
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
