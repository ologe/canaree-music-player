package dev.olog.domain.gateway

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack

interface ImageRetrieverGateway {
    suspend fun mustFetchTrack(trackId: Long): Boolean
    suspend fun getTrack(trackId: Long): LastFmTrack?
    suspend fun deleteTrack(trackId: Long)

    suspend fun mustFetchAlbum(albumId: Long): Boolean
    suspend fun getAlbum(albumId: Long): LastFmAlbum?
    suspend fun deleteAlbum(albumId: Long)

    suspend fun mustFetchArtist(artistId: Long): Boolean
    suspend fun getArtist(artistId: Long): LastFmArtist?
    suspend fun deleteArtist(artistId: Long)
} 