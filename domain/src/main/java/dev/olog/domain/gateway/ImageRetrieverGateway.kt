package dev.olog.domain.gateway

import dev.olog.domain.entity.LastFmAlbum
import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.LastFmTrack

interface ImageRetrieverGateway {

    suspend fun getCachedTrack(trackId: Long): LastFmTrack?
    suspend fun getTrack(trackId: Long): LastFmTrack?
    suspend fun deleteTrack(trackId: Long)

    suspend fun getCachedAlbum(albumId: Long): LastFmAlbum?
    suspend fun getAlbum(albumId: Long): LastFmAlbum?
    suspend fun deleteAlbum(albumId: Long)

    suspend fun getCachedArtist(artistId: Long): LastFmArtist?
    suspend fun getArtist(artistId: Long): LastFmArtist?
    suspend fun deleteArtist(artistId: Long)
} 