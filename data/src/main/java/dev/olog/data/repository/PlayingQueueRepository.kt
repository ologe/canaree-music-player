package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.entity.Song
import dev.olog.domain.gateway.PlayingQueueGateway
import dev.olog.domain.gateway.SongGateway
import dev.olog.shared.MediaId
import dev.olog.shared.groupMap
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

    private val publisher = BehaviorProcessor.create<List<Long>>()
    private val playingQueueDao = database.playingQueueDao()

    override fun getAll(): Single<List<PlayingQueueSong>> {
        return Single.concat(
                playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError()).firstOrError(),
                songGateway.getAll().firstOrError().groupMap { it.toPlayingQueueSong() }
        ).filter { it.isNotEmpty() }
                .firstOrError()
    }

    override fun observeAll(): Flowable<List<PlayingQueueSong>> {
        return playingQueueDao.getAllAsSongs(songGateway.getAll().firstOrError())
    }

    override fun update(list: List<Pair<MediaId, Long>>): Completable {
        return playingQueueDao.insert(list)
    }

    override fun updateMiniQueue(data: List<Long>) {
        publisher.onNext(data)
    }

    override fun observeMiniQueue(): Flowable<List<PlayingQueueSong>> {
        return publisher
                .observeOn(Schedulers.computation())
                .debounce(250, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .flatMapSingle { ids -> songGateway.getAll().firstOrError().flatMap { songs ->
                    val result : List<Song> = ids.asSequence()
                            .map { id -> songs.firstOrNull { it.id == id } }
                            .filter { it != null }
                            .map { it!! }
                            .toList()
                    Single.just(result)
                }}
                .groupMap { it.toPlayingQueueSong() }
//                .flatMapSingle { it.toFlowable()
//                        .flatMapMaybe { songId ->
//                            songGateway.getAll().firstOrError()
//                                    .flattenAsObservable { it }
//                                    .filter { it.id == songId }
//                                    .firstElement()
//                        }.toList()
//                }
    }

    private fun Song.toPlayingQueueSong(): PlayingQueueSong {
        return PlayingQueueSong(
                this.id,
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
                this.folder,
                this.trackNumber
        )
    }

}
