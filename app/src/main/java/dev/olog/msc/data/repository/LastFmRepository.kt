package dev.olog.msc.data.repository

import android.arch.persistence.room.EmptyResultSetException
import android.net.ConnectivityManager
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.UsedImage
import dev.olog.msc.data.db.UsedImageEntity
import dev.olog.msc.data.mapper.toDomain
import dev.olog.msc.data.mapper.toModel
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.exception.AbsentNetwork
import dev.olog.msc.utils.k.extension.isNetworkAvailable
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LastFmRepository @Inject constructor(
        @Proxy private val lastFmService: LastFmService,
        private val connectivityManager: ConnectivityManager,
        appDatabase: AppDatabase

) : LastFmGateway {

    private val dao = appDatabase.lastFmDao()

    override fun getAllImages(): List<UsedImage> {
        return dao.getAllUsedImages().map { it.toDomain() }
    }

    override fun getTrack(trackId: Long, title: String, artist: String): Single<LastFmTrack> {
        val cached = dao.getTrack(trackId).map { it.toDomain() }
                .subscribeOn(Schedulers.io())

        if (!connectivityManager.isNetworkAvailable()){
            return cached.onErrorResumeNext {
                if (it is EmptyResultSetException){
                    Single.error(AbsentNetwork())
                } else Single.error(it)
            }
        }

        val fetch = lastFmService.getTrackInfo(title, artist)
                .map {
                    dao.insertTrack(it.toModel(trackId))
                    it.toDomain(trackId)
                }
                .onErrorResumeNext { lastFmService.searchTrack(title, artist)
                        .map { it.toDomain(trackId) }
                        .flatMap { result -> lastFmService.getTrackInfo(result.title, result.artist)
                                .map {
                                    dao.insertTrack(it.toModel(trackId))
                                    it.toDomain(trackId)
                                }
                                .onErrorReturn { result }
                        }
                }

        return cached.onErrorResumeNext(fetch)
    }

    override fun getAlbum(albumId: Long, album: String, artist: String): Single<LastFmAlbum> {
        val cached = dao.getAlbum(albumId).map { it.toDomain() }
                .subscribeOn(Schedulers.io())

        if (!connectivityManager.isNetworkAvailable()){
            return cached.onErrorResumeNext {
                if (it is EmptyResultSetException){
                    Single.error(AbsentNetwork())
                } else Single.error(it)
            }
        }

        val fetch = lastFmService.getAlbumInfo(album, artist)
                .map {
                    dao.insertAlbum(it.toModel(albumId))
                    it.toDomain(albumId)
                }
                .onErrorResumeNext { lastFmService.searchAlbum(album)
                        .map { it.toDomain(albumId, artist) }
                        .flatMap { result -> lastFmService.getAlbumInfo(result.title, result.artist)
                                .map {
                                    dao.insertAlbum(it.toModel(albumId))
                                    it.toDomain(albumId)
                                }
                                .onErrorReturn { result }
                        }
                }

        return cached.onErrorResumeNext(fetch)
    }

    override fun getArtist(artistId: Long, artist: String): Single<Boolean> {
        val cached = dao.getArtist(artistId)
                .map { false }

        if (!connectivityManager.isNetworkAvailable()){
            return cached.onErrorReturn { false }
        }

        val fetch = lastFmService.getArtistInfo(artist)
                .map {
                    dao.insertArtist(it.toModel(artistId))
                    true
                }.onErrorReturn { false }

        return cached.onErrorResumeNext(fetch)
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