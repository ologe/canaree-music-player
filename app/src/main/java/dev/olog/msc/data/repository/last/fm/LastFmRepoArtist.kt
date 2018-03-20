package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.LastFmArtist
import dev.olog.msc.domain.gateway.ArtistGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepoArtist @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val artistGateway: ArtistGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(artistId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getArtist(artistId) == null }
                .subscribeOn(Schedulers.io())
    }

    fun get(artistId: Long): Single<Optional<LastFmArtist?>> {
        val cachedValue = getFromCache(artistId)

        val fetch = artistGateway.getByParam(artistId)
                .firstOrError()
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(artistId: Long): Single<Optional<LastFmArtist?>> {
        return Single.fromCallable {
            val cache = dao.getArtist(artistId)
            cache?.let { Optional.of(it.toDomain()) } ?: Optional.empty()
        }
    }

    private fun fetch(artist: Artist): Single<LastFmArtist> {
        val artistId = artist.id

        return lastFmService.getArtistInfo(artist.name)
                .map {
                    try {
                        val model = it.toModel(artistId)
                        dao.insertArtist(model)
                        it.toDomain(artistId)
                    } catch (ex: NoSuchElementException){
                        val model = LastFmNulls.createNullArtist(artistId)
                        dao.insertArtist(model)
                        throw ex
                    }
                }
    }

}