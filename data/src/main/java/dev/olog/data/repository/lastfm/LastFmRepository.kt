package dev.olog.data.repository.lastfm

import android.provider.MediaStore
import android.util.Log
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.LastFmGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.utils.awaitRepeat
import dev.olog.shared.TextUtils
import dev.olog.shared.android.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val lastFmRepoTrack: LastFmLocalTrack,
    private val lastFmRepoArtist: LastFmLocalArtist,
    private val lastFmRepoAlbum: LastFmLocalAlbum,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway

) : LastFmGateway {

    companion object {
        private val TAG = "D:${LastFmRepository::class.java.simpleName}"
    }

    // track
    override suspend fun mustFetchTrack(trackId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = lastFmRepoTrack.mustFetch(trackId)
        Log.v(TAG, "must fetch track id=$trackId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getTrack(trackId: Id): LastFmTrack? {
        Log.v(TAG, "get track id=$trackId")
        assertBackgroundThread()
        val cached = lastFmRepoTrack.getCached(trackId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$trackId")
            return cached
        }
        Log.v(TAG, "fetch id=$trackId")

        val song = songGateway.getByParam(trackId) ?: return null
        val trackTitle = TextUtils.addSpacesToDash(song.title)
        val trackArtist = if (song.artist == MediaStore.UNKNOWN_STRING) "" else song.artist

        var result: LastFmTrack? = null
        if (song.artist != MediaStore.UNKNOWN_STRING) { // search only if has artist
            result = lastFmService.getTrackInfoAsync(trackTitle, trackArtist)
                .awaitRepeat()
                ?.toDomain(trackId)
        }
        if (result == null) {
            val searchTrack = lastFmService.searchTrackAsync(trackTitle, trackArtist)
                .awaitRepeat()
                ?.toDomain(trackId)

            if (searchTrack != null && searchTrack.title.isNotBlank() && searchTrack.artist.isNotBlank()) {
                result = lastFmService.getTrackInfoAsync(searchTrack.title, searchTrack.artist)
                    .awaitRepeat()
                    ?.toDomain(trackId)
            }
            if (result == null) {
                result = LastFmNulls.createNullTrack(trackId).toDomain()
            }
        }
        lastFmRepoTrack.cache(result)
        return result
    }

    override suspend fun deleteTrack(trackId: Id) {
        assertBackgroundThread()
        lastFmRepoTrack.delete(trackId)
    }

    // album
    override suspend fun mustFetchAlbum(albumId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = lastFmRepoAlbum.mustFetch(albumId)
        Log.v(TAG, "must fetch album id=$albumId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getAlbum(albumId: Id): LastFmAlbum? {
        Log.v(TAG, "get album id=$albumId")
        assertBackgroundThread()
        val album = albumGateway.getByParam(albumId) ?: return null
        if (album.hasSameNameAsFolder) {
            Log.v(TAG, "id=$albumId has same name as folder, skip")
            return null
        }

        val cached = lastFmRepoAlbum.getCached(albumId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$album")
            return cached
        }
        Log.v(TAG, "fetch id=$albumId")

        var result: LastFmAlbum? = null
        if (album.title != MediaStore.UNKNOWN_STRING) {
            result = lastFmService.getAlbumInfoAsync(album.title, album.artist).awaitRepeat()
                ?.toDomain(albumId)
        }

        if (result == null) {
            val searchAlbum = lastFmService.searchAlbumAsync(album.title).awaitRepeat()
                ?.toDomain(albumId, album.artist)

            if (searchAlbum != null && searchAlbum.title.isNotBlank() && searchAlbum.artist.isNotBlank()) {
                result = lastFmService.getAlbumInfoAsync(searchAlbum.title, searchAlbum.artist)
                    .awaitRepeat()
                    ?.toDomain(albumId)
            }
            if (result == null) {
                result = LastFmNulls.createNullAlbum(albumId).toDomain()
            }
        }
        lastFmRepoAlbum.cache(result)
        return result
    }

    override suspend fun deleteAlbum(albumId: Id) {
        assertBackgroundThread()
        lastFmRepoAlbum.delete(albumId)
    }

    // artist
    override suspend fun mustFetchArtist(artistId: Id): Boolean {
        assertBackgroundThread()
        val mustFetch = lastFmRepoArtist.mustFetch(artistId)
        Log.v(TAG, "must fetch artist id=$artistId -> $mustFetch")
        return mustFetch
    }

    override suspend fun getArtist(artistId: Id): LastFmArtist? {
        Log.v(TAG, "get artist id=$artistId")
        assertBackgroundThread()
        val cached = lastFmRepoArtist.getCached(artistId)
        if (cached != null) {
            Log.v(TAG, "found in cache id=$artistId")
            return cached
        }
        Log.v(TAG, "fetch id=$artistId")

        val artist = artistGateway.getByParam(artistId) ?: return null
        var result = lastFmService.getArtistInfoAsync(artist.name).awaitRepeat()?.toDomain(artistId)
        if (result == null) {
            result = LastFmNulls.createNullArtist(artistId).toDomain()
        }
        lastFmRepoArtist.cache(result)
        return result
    }

    override suspend fun deleteArtist(artistId: Id) {
        assertBackgroundThread()
        lastFmRepoArtist.delete(artistId)
    }
}