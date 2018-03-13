package dev.olog.msc.data.repository

import com.github.dmstocking.optional.java.util.Optional
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.UsedImage
import dev.olog.msc.data.db.UsedImageEntity
import dev.olog.msc.data.mapper.LastFmNulls
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.exception.AbsentNetwork
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        @Proxy private val lastFmService: LastFmService,
//        private val connectivityManager: ConnectivityManager,
        appDatabase: AppDatabase

) : LastFmGateway {

    private val dao = appDatabase.lastFmDao()

    override fun getAllImages(): List<UsedImage> {
        return dao.getAllUsedImages().map { it.toDomain() }
    }

    /*
        todo adjust this and callers
     */
    override fun getTrack(trackId: Long, title: String, artist: String, album: String): Single<Optional<out LastFmTrack?>> {
        val cachedValue = Single.fromCallable { Optional.ofNullable(dao.getTrack(trackId, title, artist, album)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get().toDomain())
                    } else Optional.ofNullable(null)
                }

        val fetch = lastFmService.getTrackInfo(title, artist)
                .map {
                    val res = it.toDomain(trackId)
                    dao.insertTrack(res.toModel())
                    res
                }
                .onErrorResumeNext { lastFmService.searchTrack(title, artist)
                        .map {
                            val res = it.toDomain(trackId)
                            dao.insertTrack(res.toModel())
                            res
                        }
                        .flatMap { result -> lastFmService.getTrackInfo(result.title, result.artist)
                                .map {
                                    val res = it.toDomain(trackId)
                                    dao.insertTrack(res.toModel())
                                    res
                                }
                                .onErrorReturn { result }
                        }.doOnError {
                            if (it is NoSuchElementException){
                                dao.insertTrack(LastFmNulls.createNullTrack(trackId))
                            }
                        }
                }

        return Singles.zip(ReactiveNetwork.checkInternetConnectivity(), cachedValue) { isConnected, cached ->
            when {
                !cached.isPresent && isConnected -> true to cached
                !cached.isPresent && isConnected -> throw AbsentNetwork()
                else -> false to cached
            }
        }.flatMap { (shouldFetch, track) ->
            if (shouldFetch){
                fetch.map { Optional.ofNullable(it) }
            } else Single.just(track)
        }.subscribeOn(Schedulers.io())
    }

    /*
        todo adjust this and callers
     */
    override fun getAlbum(albumId: Long, album: String, artist: String): Single<Optional<out LastFmAlbum?>> {
        val cachedValue = Single.fromCallable { Optional.ofNullable(dao.getAlbum(albumId, album, artist)) }
                .map {
                    if (it.isPresent){
                        Optional.of(it.get().toDomain())
                    } else Optional.ofNullable(null)
                }

        val fetch = lastFmService.getAlbumInfo(album, artist)
                .map {
                    val res = it.toDomain(albumId)
                    dao.insertAlbum(res.toModel())
                    res
                }
                .onErrorResumeNext { lastFmService.searchAlbum(album)
                        .map {
                            val res = it.toDomain(albumId, artist)
                            dao.insertAlbum(res.toModel())
                            res
                        }
                        .flatMap { result -> lastFmService.getAlbumInfo(result.title, result.artist)
                                .map {
                                    val res = it.toDomain(albumId)
                                    dao.insertAlbum(res.toModel())
                                    res
                                }
                                .onErrorReturn { result }
                        }
                }.doOnError {
                    if (it is NoSuchElementException){
                        dao.insertAlbum(LastFmNulls.createNullAlbum(albumId))
                    }
                }

        return Singles.zip(ReactiveNetwork.checkInternetConnectivity(), cachedValue) { isConnected, cached ->
            when {
                !cached.isPresent && isConnected -> true to cached
                !cached.isPresent && !isConnected -> throw AbsentNetwork()
                else -> false to cached
            }
        }.flatMap { (shouldFetch, album) ->
            if (shouldFetch){
                fetch.map { Optional.ofNullable(it) }
            } else {
                Single.just(album)
            }
        }.subscribeOn(Schedulers.io())
    }

    /**
     * @return true if no cache exists
     */
    override fun shouldFetchArtist(artistId: Long): Single<Boolean> {
        return Single.fromCallable { Optional.ofNullable(dao.getArtist(artistId)) }
                .map { !it.isPresent }
    }

    /**
     * @returns true if no cache exists and is fetched
     */
    override fun getArtist(artistId: Long, artist: String): Single<Boolean> {
        val cachedValue = Single.fromCallable { Optional.ofNullable(dao.getArtist(artistId)) }

        val fetch = lastFmService.getArtistInfo(artist)
                .map {
                    dao.insertArtist(it.toModel(artistId))
                    true
                }.doOnError {
                    if (it is NoSuchElementException){
                        dao.insertArtist(LastFmNulls.createNullArtist(artistId))
                    }
                }.onErrorReturnItem(false)

        return Singles.zip(ReactiveNetwork.checkInternetConnectivity(), cachedValue) { isConnected, cached ->
            when {
                !cached.isPresent && isConnected -> true
                else -> false
            }
        }.flatMap { shouldFetch ->
            if (shouldFetch){
                fetch
            } else Single.just(false)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertTrackImage(trackId: Long, image: String): Completable {
        return Completable.fromCallable {
            dao.insertUsedTrackImage(UsedImageEntity(trackId, false, image))
        }.subscribeOn(Schedulers.io())
    }

    override fun insertAlbumImage(albumId: Long, image: String): Completable {
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