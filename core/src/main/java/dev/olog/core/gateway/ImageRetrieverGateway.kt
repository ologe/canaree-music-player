package dev.olog.core.gateway

import dev.olog.core.IoResult
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.base.Id

interface ImageRetrieverGateway {

    suspend fun getSong(trackId: Id): IoResult<LastFmTrack?>
    suspend fun fetchSongImage(trackId: Id): ImageRetrieverResult<String>

    suspend fun getAlbum(albumId: Id): IoResult<LastFmAlbum?>
    suspend fun fetchAlbumImage(albumId: Id): ImageRetrieverResult<String>

    suspend fun getArtist(artistId: Id): IoResult<LastFmArtist?>
    suspend fun fetchArtistImage(artistId: Id): ImageRetrieverResult<String>

} 