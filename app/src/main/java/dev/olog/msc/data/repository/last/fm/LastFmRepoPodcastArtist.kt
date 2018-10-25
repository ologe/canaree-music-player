package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.api.last.fm.artist.info.ArtistInfo
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmPodcastArtistEntity
import dev.olog.msc.data.mapper.*
import dev.olog.msc.domain.entity.LastFmPodcastArtist
import dev.olog.msc.domain.entity.PodcastArtist
import dev.olog.msc.domain.gateway.PodcastArtistGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepoPodcastArtist @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val artistGateway: PodcastArtistGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(artistId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getPodcastArtist(artistId) == null }
                .subscribeOn(Schedulers.io())
    }

    fun get(artistId: Long): Single<Optional<LastFmPodcastArtist?>> {
        val cachedValue = getFromCache(artistId)

        val fetch = artistGateway.getByParam(artistId)
                .firstOrError()
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(artistId: Long): Single<Optional<LastFmPodcastArtist?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getPodcastArtist(artistId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(artist: PodcastArtist): Single<LastFmPodcastArtist> {
        val artistId = artist.id

        return lastFmService.getArtistInfo(artist.name)
                .map {
                    try {
                        cache(artistId, it)
                        val model = it.toPodcastModel(artistId)
                        dao.insertPodcastArtist(model)
                        it.toPodcastDomain(artistId)
                    } catch (ex: NoSuchElementException){
                        cacheEmpty(artistId)
                        throw ex
                    }
                }
    }

    private fun cache(artistId: Long, model: ArtistInfo): LastFmPodcastArtistEntity{
        val entity = model.toPodcastModel(artistId)
        dao.insertPodcastArtist(entity)
        return entity
    }

    private fun cacheEmpty(artistId: Long): LastFmPodcastArtistEntity{
        val entity = LastFmNulls.createNullPodcastArtist(artistId)
        dao.insertPodcastArtist(entity)
        return entity
    }

    fun delete(artistId: Long) {
        dao.deleteArtist(artistId)
    }

}