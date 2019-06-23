package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.api.last.fm.artist.info.ArtistInfo
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.LastFmArtistEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.gateway.ArtistGateway2
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class LastFmRepoArtist @Inject constructor(
    appDatabase: AppDatabase,
    @Proxy private val lastFmService: LastFmService,
    private val artistGateway: ArtistGateway2

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(artistId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getArtist(artistId) == null }
                .subscribeOn(Schedulers.io())
    }

    fun get(artistId: Long): Single<Optional<LastFmArtist?>> {
        val cachedValue = getFromCache(artistId)

        val fetch = artistGateway.observeByParam(artistId).map { it!! }.asObservable()
                .firstOrError()
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(artistId: Long): Single<Optional<LastFmArtist?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getArtist(artistId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(artist: Artist): Single<LastFmArtist> {
        val artistId = artist.id

        return lastFmService.getArtistInfo(artist.name)
                .map {
                    try {
                        cache(artistId, it)
                        val model = it.toModel(artistId)
                        dao.insertArtist(model)
                        it.toDomain(artistId)
                    } catch (ex: NoSuchElementException){
                        cacheEmpty(artistId)
                        throw ex
                    }
                }
    }

    private fun cache(artistId: Long, model: ArtistInfo): LastFmArtistEntity {
        val entity = model.toModel(artistId)
        dao.insertArtist(entity)
        return entity
    }

    private fun cacheEmpty(artistId: Long): LastFmArtistEntity {
        val entity = LastFmNulls.createNullArtist(artistId)
        dao.insertArtist(entity)
        return entity
    }

    fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}