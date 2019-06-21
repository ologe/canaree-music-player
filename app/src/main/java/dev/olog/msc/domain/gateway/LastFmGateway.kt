package dev.olog.msc.domain.gateway

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import io.reactivex.Single

interface LastFmGateway {

    fun shouldFetchTrack(trackId: Long): Single<Boolean>
    fun getTrack(trackId: Long): Single<Optional<LastFmTrack?>>
    fun deleteTrack(trackId: Long)

    fun shouldFetchTrackImage(trackId: Long): Single<Boolean>
    fun getTrackImage(trackId: Long): Single<Optional<String?>>

    fun shouldFetchAlbum(albumId: Long): Single<Boolean>
    fun getAlbum(albumId: Long): Single<Optional<LastFmAlbum?>>
    fun deleteAlbum(albumId: Long)

    fun shouldFetchArtist(artistId: Long): Single<Boolean>
    fun getArtist(artistId: Long): Single<Optional<LastFmArtist?>>
    fun deleteArtist(artistId: Long)

}