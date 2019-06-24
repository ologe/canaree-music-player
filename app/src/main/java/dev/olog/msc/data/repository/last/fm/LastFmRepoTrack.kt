package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.presentation.AppConstants
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.LastFmTrackEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.SongGateway2
import dev.olog.shared.utils.TextUtils
import dev.olog.shared.utils.assertBackgroundThread
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class LastFmRepoTrack @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val songGateway: SongGateway2

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(trackId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getTrack(trackId) == null }
    }

    fun getOriginalItem(trackId: Long): Single<Song> {
        return songGateway.observeByParam(trackId).map { it!! }.asObservable().firstOrError()
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
        assertBackgroundThread()

        val trackId = track.id

        val trackTitle = TextUtils.addSpacesToDash(track.title)
        val trackArtist = if (track.artist == AppConstants.UNKNOWN) "" else track.artist

        return lastFmService.getTrackInfo(trackTitle, trackArtist)
                .map { it.toDomain(trackId) }
                .doOnSuccess { cache(it) }
                .onErrorResumeNext { lastFmService.searchTrack(trackTitle, trackArtist)
                        .map { it.toDomain(trackId) }
                        .flatMap { result -> lastFmService.getTrackInfo(result.title, result.artist)
                                .map { it.toDomain(trackId) }
                                .onErrorReturnItem(result)
                        }
                        .doOnSuccess { cache(it) }
                        .onErrorResumeNext {
                            if (it is NoSuchElementException){
                                Single.fromCallable { cacheEmpty(trackId) }
                                        .map { it.toDomain() }
                            } else Single.error(it)
                        }
                }
    }

    private fun cache(model: LastFmTrack): LastFmTrackEntity {
        val entity = model.toModel()
        dao.insertTrack(entity)
        return entity
    }

    private fun cacheEmpty(trackId: Long): LastFmTrackEntity {
        val entity = LastFmNulls.createNullTrack(trackId)
        dao.insertTrack(entity)
        return entity
    }

    fun delete(trackId: Long){
        dao.deleteTrack(trackId)
    }

}