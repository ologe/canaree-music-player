package dev.olog.core.gateway

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack

interface LastFmGateway2 {
    suspend fun shouldFetchTrack(trackId: Id): Boolean
    suspend fun getTrack(trackId: Id): LastFmTrack?
    suspend fun deleteTrack(trackId: Id)

    suspend fun shouldFetchTrackImage(trackId: Id): Boolean
    suspend fun getTrackImage(trackId: Id): String?

    suspend fun shouldFetchAlbum(albumId: Id): Boolean
    suspend fun getAlbum(albumId: Id): LastFmAlbum?
    suspend fun deleteAlbum(albumId: Id)

    suspend fun shouldFetchArtist(artistId: Id): Boolean
    suspend fun getArtist(artistId: Id): LastFmArtist?
    suspend fun deleteArtist(artistId: Id)
} 