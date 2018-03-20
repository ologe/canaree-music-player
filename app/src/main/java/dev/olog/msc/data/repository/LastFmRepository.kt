package dev.olog.msc.data.repository

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.UsedImage
import dev.olog.msc.data.db.UsedImageEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmArtist
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.k.extension.defer
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        @Proxy private val lastFmService: LastFmService,
        private val albumGateway: AlbumGateway,
        private val artistGateway: ArtistGateway,
        private val songGateway: SongGateway,
        appDatabase: AppDatabase

) : LastFmGateway {

    private val dao = appDatabase.lastFmDao()

    override fun shouldFetchTrack(trackId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getTrack(trackId) == null }
    }

    override fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>> {
        val cachedValue = Single.fromCallable { Optional.ofNullable(dao.getTrack(trackId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }

        val fetch = songGateway.getByParam(trackId)
                .firstOrError()
                .flatMap { track -> lastFmService.getTrackInfo(track.title, track.artist)
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
                }.map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())

//        return Singles.zip(ReactiveNetwork.checkInternetConnectivity(), cachedValue) { isConnected, cached ->
//            when {
//                !cached.isPresent && isConnected -> true to cached
//                !cached.isPresent && !isConnected -> throw NoNetworkException()
//                else -> false to cached
//            }
//        }.flatMap { (shouldFetch, track) ->
//            when {
//                shouldFetch -> fetch.map { Optional.ofNullable(it) }
//                else -> Single.just(track)
//            }.throwIfNull()
//        }
    }

    override fun shouldFetchAlbum(albumId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getAlbum(albumId) == null }
    }

    override fun getAlbum(albumId: Long): Single<Optional<LastFmAlbum?>> {
        val cachedValue = Single.fromCallable { Optional.ofNullable(dao.getAlbum(albumId)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get()!!.toDomain())
                    } else throw NoSuchElementException()
                }

        val fetch = albumGateway.getByParam(albumId)
                .firstOrError()
                .flatMap { album ->
                    lastFmService.getAlbumInfo(album.title, album.artist)
                            .map { it.toDomain(albumId) }
                            .doOnSuccess { dao.insertAlbum(it.toModel(it.title, it.artist)) }
                            .onErrorResumeNext { lastFmService.searchAlbum(album.title)
                                    .map { it.toDomain(albumId, album.artist) }
                                    .flatMap { result -> lastFmService.getAlbumInfo(result.title, result.artist)
                                            .map { it.toDomain(albumId) }
                                            .onErrorReturnItem(result)
                                    }
                                    .doOnSuccess { dao.insertAlbum(it.toModel(it.title, it.artist)) }
                                    .onErrorResumeNext {
                                        if (it is NoSuchElementException){
                                            Single.just(LastFmNulls.createNullAlbum(albumId))
                                                    .doOnSuccess { dao.insertAlbum(it) }
                                                    .map { it.toDomain() }
                                        } else Single.error(it)
                                    }
                            }
                }.map { Optional.of(it) }

        return cachedValue.onErrorResumeNext(fetch)
                .subscribeOn(Schedulers.io())

//        return Singles.zip(ReactiveNetwork.checkInternetConnectivity(), cachedValue) { isConnected, cached ->
//            when {
//                !cached.isPresent -> true to cached
//                else -> false to cached
//            }
//        }.flatMap { (shouldFetch, album) ->
//            when {
//                shouldFetch -> fetch.map { Optional.ofNullable(it) }
//                else -> Single.just(album)
//            }.throwIfNull()
//        }
    }

    override fun shouldFetchArtist(artistId: Long): Single<Boolean> {
        return Single.fromCallable { dao.getArtist(artistId) == null }
                .subscribeOn(Schedulers.io())
    }

    override fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>> {
        val cachedValue = Single.fromCallable {
            val cache = dao.getArtist(artistId)
            cache?.let { Optional.of(it.toDomain()) } ?: Optional.empty()
        }

        val fetch = artistGateway.getByParam(artistId)
                .firstOrError()
                .flatMap { lastFmService.getArtistInfo(it.name) }
                .map {
                    dao.insertArtist(it.toModel(artistId))
                    Optional.of(it.toDomain(artistId))
                }.onErrorResumeNext {
                    var single = Single.just(Optional.empty<LastFmArtist>())
                    if (it is NoSuchElementException){
                        single = single.doOnSuccess { dao.insertArtist(LastFmNulls.createNullArtist(artistId)) }
                    }
                    single
                }.defer()

        return cachedValue.flatMap {
            if (!it.isPresent){
                fetch
            } else Single.just(it)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertTrackImage(trackId: Long, image: String): Completable {
        if (image.isBlank()){
            return Completable.complete()
        }
        return Completable.fromCallable {
            dao.insertUsedTrackImage(UsedImageEntity(trackId, false, image))
        }.subscribeOn(Schedulers.io())
    }

    override fun insertAlbumImage(albumId: Long, image: String): Completable {
        if (image.isBlank()){
            return Completable.complete()
        }
        return Completable.fromCallable {
            dao.insertUsedAlbumImage(UsedImageEntity(albumId, true, image))
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTrackImage(trackId: Long): Completable {
        return Completable.fromCallable {
            dao.deleteUsedTrackImage(trackId)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteAlbumImage(albumId: Long): Completable {
        return Completable.fromCallable {
            dao.deleteUsedAlbumImage(albumId)
        }.subscribeOn(Schedulers.io())
    }

    private fun UsedImageEntity.toDomain(): UsedImage {
        return UsedImage(
                this.id,
                this.isAlbum,
                this.image
        )
    }

}