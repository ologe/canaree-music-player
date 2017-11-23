package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class PlayingQueueRepository @Inject constructor(
        database: AppDatabase,
        private val songGateway: SongGateway

) : PlayingQueueGateway {

    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<Song>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError()),
                songGateway.getAll().firstOrError()
        ).filter { it.isNotEmpty() }
                .firstOrError()
    }

    override fun update(list: List<Long>): Completable {
        return Completable.fromCallable { playingQueueDao.insert(list) }
    }

    override fun observeMiniQueue(): Flowable<List<Song>> {
        return Flowable.just(listOf()) // todo
    }
}
