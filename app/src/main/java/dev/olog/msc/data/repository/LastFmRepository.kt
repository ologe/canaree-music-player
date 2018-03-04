package dev.olog.msc.data.repository

import android.accounts.NetworkErrorException
import android.arch.persistence.room.EmptyResultSetException
import android.net.ConnectivityManager
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.api.last.fm.album.info.AlbumInfo
import dev.olog.msc.api.last.fm.album.search.AlbumSearch
import dev.olog.msc.api.last.fm.annotation.Proxy
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.UsedImage
import dev.olog.msc.data.db.UsedImageEntity
import dev.olog.msc.data.entity.LastFmAlbumEntity
import dev.olog.msc.data.entity.LastFmTrackEntity
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.utils.k.extension.isNetworkAvailable
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import me.xdrop.fuzzywuzzy.FuzzySearch
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
                    Single.error(NetworkErrorException())
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
                    Single.error(NetworkErrorException())
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

    private fun LastFmTrackEntity.toDomain(): LastFmTrack {
        return LastFmTrack(
                this.id,
                this.title,
                this.artist,
                this.album,
                this.image
        )
    }

    private fun LastFmAlbumEntity.toDomain(): LastFmAlbum {
        return LastFmAlbum(
                this.id,
                this.title,
                this.artist,
                this.image
        )
    }

    private fun TrackInfo.toDomain(id: Long): LastFmTrack {
        val track = this.track
        val title = track.name
        val artist = track.artist.name
        val album = track.album.title
        val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

        return LastFmTrack(
                id,
                title ?: "",
                artist ?: "",
                album ?: "",
                image
        )
    }

    private fun TrackInfo.toModel(id: Long): LastFmTrackEntity {
        val track = this.track
        val title = track.name
        val artist = track.artist.name
        val album = track.album.title
        val image = track.album.image.reversed().first { it.text.isNotBlank() }.text

        return LastFmTrackEntity(
                id,
                title ?: "",
                artist ?: "",
                album ?: "",
                image
        )
    }

    private fun AlbumInfo.toDomain(id: Long): LastFmAlbum {
        val album = this.album
        return LastFmAlbum(
                id,
                album.name,
                album.artist,
                album.image.reversed().first { it.text.isNotBlank() }.text
        )
    }

    private fun AlbumInfo.toModel(id: Long): LastFmAlbumEntity {
        val album = this.album
        return LastFmAlbumEntity(
                id,
                album.name,
                album.artist,
                album.image.reversed().first { it.text.isNotBlank() }.text
        )
    }

    private fun TrackSearch.toDomain(id: Long): LastFmTrack {
        val track = this.results.trackmatches.track[0]

        return LastFmTrack(
                id,
                track.name ?: "",
                track.artist ?: "",
                "",
                ""
        )
    }

    private fun AlbumSearch.toDomain(id: Long, originalArtist: String): LastFmAlbum {
        val results = this.results.albummatches.album
        val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
        val best = results.first { it.artist == bestArtist }

        return LastFmAlbum(
                id,
                best.name,
                best.artist,
                best.image.reversed().first { it.text.isNotBlank() }.text
        )
    }

    private fun UsedImageEntity.toDomain(): UsedImage {
        return UsedImage(
                this.id,
                this.isAlbum,
                this.image
        )
    }

}