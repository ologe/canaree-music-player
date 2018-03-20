package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepoTrack @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val songGateway: SongGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(trackId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getTrack(trackId) == null }
    }

    fun getOriginalItem(trackId: Long): Single<Song> {
        return songGateway.getByParam(trackId).firstOrError()
    }

    fun get(trackId: Long): Single<Optional<LastFmTrack?>> {
        val cachedValue = getFromCache(trackId)

        val fetch = getOriginalItem(trackId)
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(trackId: Long): Single<Optional<LastFmTrack?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getTrack(trackId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(track: Song): Single<LastFmTrack> {
        val trackId = track.id

        return lastFmService.getTrackInfo(track.title, track.artist)
                .map { it.toDomain(trackId) }
                .doOnSuccess { dao.insertTrack(it.toModel(track.title, track.artist, track.album)) }
                .onErrorResumeNext { lastFmService.searchTrack(track.title, track.artist)
                        .map { it.toDomain(trackId) }
                        .flatMap { result -> lastFmService.getTrackInfo(result.title, result.artist)
                                .map { it.toDomain(trackId) }
                                .onErrorReturnItem(result)
                        }
                        .doOnSuccess { dao.insertTrack(it.toModel(track.title, track.artist, track.album)) }
                        .onErrorResumeNext {
                            if (it is NoSuchElementException){
                                Single.just(LastFmNulls.createNullTrack(trackId))
                                        .doOnSuccess { dao.insertTrack(it) }
                                        .map { it.toDomain() }
                            } else Single.error(it)
                        }
                }
    }

}