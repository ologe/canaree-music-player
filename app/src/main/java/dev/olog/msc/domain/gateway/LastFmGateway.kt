package dev.olog.msc.domain.gateway

import dev.olog.msc.data.db.UsedImage
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.LastFmTrack
import io.reactivex.Completable
import io.reactivex.Single

interface LastFmGateway {

    fun getAllImages(): List<UsedImage>

    fun getTrack(trackId: Long, title: String, artist: String): Single<LastFmTrack>
    fun getAlbum(albumId: Long, album: String, artist: String): Single<LastFmAlbum>

    fun insertTrackImage(trackId: Long, image: String): Completable
    fun insertAlbumImage(albumId: Long, image: String): Completable

    fun deleteTrackImage(trackId: Long): Completable
    fun deleteAlbumImage(albumId: Long): Completable

}