package dev.olog.msc.data.repository.last.fm

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.LastFmPodcastAlbumEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.data.mapper.toPodcastDomain
import dev.olog.msc.domain.entity.LastFmPodcastAlbum
import dev.olog.core.entity.PodcastAlbum
import dev.olog.msc.domain.gateway.PodcastAlbumGateway
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepoPodcastAlbum @Inject constructor(
        appDatabase: AppDatabase,
        @Proxy private val lastFmService: LastFmService,
        private val albumGateway: PodcastAlbumGateway

) {

    private val dao = appDatabase.lastFmDao()

    fun shouldFetch(albumId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getPodcastAlbum(albumId) == null }
    }

    fun get(albumId: Long): Single<Optional<LastFmPodcastAlbum?>> {
        val cachedValue = getFromCache(albumId)

        val fetch = albumGateway.getByParam(albumId)
                .firstOrError()
                .flatMap {
                    if (it.hasSameNameAsFolder){
                        Single.error(Exception("image not downloadable"))
                    } else {
                        Single.just(it)
                    }
                }
                .flatMap { fetch(it) }
                .map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())
    }

    private fun getFromCache(albumId: Long): Single<Optional<LastFmPodcastAlbum?>> {
        return Single.fromCallable { Optional.ofNullable(dao.getPodcastAlbum(albumId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }
    }

    private fun fetch(album: PodcastAlbum): Single<LastFmPodcastAlbum> {
        val albumId = album.id

        return lastFmService.getAlbumInfo(album.title, album.artist)
                .map { it.toPodcastDomain(albumId) }
                .doOnSuccess { cache(it) }
                .onErrorResumeNext { lastFmService.searchAlbum(album.title)
                        .map { it.toPodcastDomain(albumId, album.artist) }
                        .flatMap { result -> lastFmService.getAlbumInfo(result.title, result.artist)
                                .map { it.toPodcastDomain(albumId) }
                                .onErrorReturnItem(result)
                        }
                        .doOnSuccess { cache(it) }
                        .onErrorResumeNext {
                            if (it is NoSuchElementException){
                                Single.fromCallable { cacheEmpty(albumId) }
                                        .map { it.toDomain() }
                            } else Single.error(it)
                        }
                }
    }

    private fun cache(model: LastFmPodcastAlbum): LastFmPodcastAlbumEntity {
        val entity = model.toModel()
        dao.insertPodcastAlbum(entity)
        return entity
    }

    private fun cacheEmpty(albumId: Long): LastFmPodcastAlbumEntity{
        val entity = LastFmNulls.createNullPodcastAlbum(albumId)
        dao.insertPodcastAlbum(entity)
        return entity
    }

    fun delete(albumId: Long) {
        dao.deletePodcastAlbum(albumId)
    }

}