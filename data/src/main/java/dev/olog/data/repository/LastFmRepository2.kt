package dev.olog.data.repository

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.LastFmGateway2

internal class LastFmRepository2 : LastFmGateway2 {

    override suspend fun shouldFetchTrack(trackId: Id): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTrack(trackId: Id): LastFmTrack? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteTrack(trackId: Id) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun shouldFetchTrackImage(trackId: Id): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getTrackImage(trackId: Id): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun shouldFetchAlbum(albumId: Id): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getAlbum(albumId: Id): LastFmAlbum? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAlbum(albumId: Id) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun shouldFetchArtist(artistId: Id): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getArtist(artistId: Id): LastFmArtist? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteArtist(artistId: Id) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}