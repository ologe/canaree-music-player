package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaId
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayingQueueRepository @Inject constructor(
        database: AppDatabase,
        private val songGateway: SongGateway

) : PlayingQueueGateway {

    private val publisher = BehaviorProcessor.create<List<PlayingQueueSong>>()
    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError()).firstOrError(),
                songGateway.getAll().firstOrError()
                        .map { it.mapIndexed { index, song-> song.toPlayingQueueSong(index) } }
        ).filter { it.isNotEmpty() }.firstOrError()
    }

    override fun observeAll(): Flowable<List<PlayingQueueSong>> {
        return playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError())
    }

    override fun update(list: List<Pair<MediaId, Long>>): Completable {
        return playingQueueDao.insert(list)
    }

    override fun updateMiniQueue(data: List<PlayingQueueSong>) {
        publisher.onNext(data)
    }

    override fun observeMiniQueue(): Flowable<List<PlayingQueueSong>> {
        return publisher
                .observeOn(Schedulers.computation())
                .debounce(250, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
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
                this.album,
                this.image,
                this.duration,
                this.dateAdded,
                this.isRemix,
                this.isExplicit,
                this.path,
                this.trackNumber
        )
    }

}
