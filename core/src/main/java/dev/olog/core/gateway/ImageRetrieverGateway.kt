package dev.olog.core.gateway

import dev.olog.core.MediaUri
import dev.olog.core.last.fm.LastFmAlbum
import dev.olog.core.last.fm.LastFmArtist
import dev.olog.core.last.fm.LastFmTrack

interface ImageRetrieverGateway {
    suspend fun mustFetchTrack(uri: MediaUri): Boolean
    suspend fun getTrack(uri: MediaUri): LastFmTrack?
    suspend fun deleteTrack(uri: MediaUri)

    suspend fun mustFetchAlbum(uri: MediaUri): Boolean
    suspend fun getAlbum(uri: MediaUri): LastFmAlbum?
    suspend fun deleteAlbum(uri: MediaUri)

    suspend fun mustFetchArtist(uri: MediaUri): Boolean
    suspend fun getArtist(uri: MediaUri): LastFmArtist?
    suspend fun deleteArtist(uri: MediaUri)
} 