package dev.olog.domain.gateway

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.gateway.base.Id

interface ImageRetrieverGateway {
    suspend fun mustFetchTrack(trackId: Id): Boolean
    suspend fun getTrack(trackId: Id): LastFmTrack?
    suspend fun deleteTrack(trackId: Id)

    suspend fun mustFetchAlbum(albumId: Id): Boolean
    suspend fun getAlbum(albumId: Id): LastFmAlbum?
    suspend fun deleteAlbum(albumId: Id)

    suspend fun mustFetchArtist(artistId: Id): Boolean
    suspend fun getArtist(artistId: Id): LastFmArtist?
    suspend fun deleteArtist(artistId: Id)
} 