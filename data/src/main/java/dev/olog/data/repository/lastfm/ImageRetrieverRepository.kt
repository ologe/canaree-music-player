package dev.olog.data.repository.lastfm

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.local.last.fm.ImageRetrieverLocalAlbum
import dev.olog.data.local.last.fm.ImageRetrieverLocalArtist
import dev.olog.data.local.last.fm.ImageRetrieverLocalTrack
import dev.olog.data.remote.ImageRetrieverRemoteAlbum
import dev.olog.data.remote.ImageRetrieverRemoteArtist
import dev.olog.data.remote.ImageRetrieverRemoteTrack
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class ImageRetrieverRepository @Inject constructor(
    // track
    private val localTrack: ImageRetrieverLocalTrack,
    private val remoteTrack: ImageRetrieverRemoteTrack,
    // artist
    private val localArtist: ImageRetrieverLocalArtist,
    private val remoteArtist: ImageRetrieverRemoteArtist,
    // album
    private val localAlbum: ImageRetrieverLocalAlbum,
    private val remoteAlbum: ImageRetrieverRemoteAlbum,
    // gateway
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway
) : ImageRetrieverGateway {

    override suspend fun mustFetchTrack(trackId: Id): Boolean = localTrack.mustFetch(trackId)

    override suspend fun getTrack(trackId: Id): LastFmTrack? {
        val cached = localTrack.getCached(trackId)
        if (cached != null) {
            return cached
        }

        val song = songGateway.getByParam(trackId) ?: return null
        val result = remoteTrack.fetch(song)

        localTrack.cache(result)
        return result
    }

    override suspend fun deleteTrack(trackId: Id) {
        localTrack.delete(trackId)
    }

    // region album
    override suspend fun mustFetchAlbum(albumId: Id): Boolean = localAlbum.mustFetch(albumId)

    override suspend fun getAlbum(albumId: Id): LastFmAlbum? {
        val album = albumGateway.getByParam(albumId) ?: return null
        if (album.hasSameNameAsFolder) {
            return null
        }

        val cached = localAlbum.getCached(albumId)
        if (cached != null) {
            return cached
        }

        val result = remoteAlbum.fetch(album)
        localAlbum.cache(result)
        return result
    }

    override suspend fun deleteAlbum(albumId: Id) {
        localAlbum.delete(albumId)
    }

    // endregion

    // region artist
    override suspend fun mustFetchArtist(artistId: Id): Boolean {
        return localArtist.mustFetch(artistId)
    }

    override suspend fun getArtist(artistId: Id): LastFmArtist? = coroutineScope {
        val cached = localArtist.getCached(artistId)
        if (cached != null) {
            return@coroutineScope cached
        }

        val artist = artistGateway.getByParam(artistId) ?: return@coroutineScope null

        val result = remoteArtist.fetch(artist)

        localArtist.cache(result)
        return@coroutineScope result
    }



    override suspend fun deleteArtist(artistId: Id) {
        localArtist.delete(artistId)
    }

    // endregion

}