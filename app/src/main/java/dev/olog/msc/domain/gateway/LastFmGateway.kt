package dev.olog.msc.domain.gateway

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmArtist
import dev.olog.msc.domain.entity.LastFmTrack
import io.reactivex.Completable
import io.reactivex.Single

interface LastFmGateway {

    fun shouldFetchTrack(trackId: Long): Single<Boolean>
    fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>>

    fun shouldFetchAlbum(albumId: Long): Single<Boolean>
    fun getAlbum(albumId: Long): Single<Optional<LastFmAlbum?>>

    fun shouldFetchArtist(artistId: Long): Single<Boolean>
    fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>>

    fun insertTrackImage(trackId: Long, image: String): Completable
    fun insertAlbumImage(albumId: Long, image: String): Completable

    fun deleteTrackImage(trackId: Long): Completable
    fun deleteAlbumImage(albumId: Long): Completable

}