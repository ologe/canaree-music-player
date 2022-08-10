package dev.olog.core.gateway

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack

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