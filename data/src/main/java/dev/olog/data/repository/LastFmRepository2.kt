package dev.olog.data.repository

import android.provider.MediaStore
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.gateway.*
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.mapper.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.repository.lastfm.LastFmLocalAlbum
import dev.olog.data.repository.lastfm.LastFmLocalArtist
import dev.olog.data.repository.lastfm.LastFmLocalTrack
import dev.olog.data.utils.awaitRepeat
import dev.olog.shared.utils.TextUtils
import dev.olog.shared.utils.assertBackgroundThread
import javax.inject.Inject

internal class LastFmRepository2 @Inject constructor(
    private val lastFmService: LastFmService,
    private val lastFmRepoTrack: LastFmLocalTrack,
    private val lastFmRepoArtist: LastFmLocalArtist,
    private val lastFmRepoAlbum: LastFmLocalAlbum,
    private val songGateway: SongGateway2,
    private val albumGateway: AlbumGateway2,
    private val artistGateway: ArtistGateway2

) : LastFmGateway2 {

    // track
    override suspend fun mustFetchTrack(trackId: Id): Boolean {
        assertBackgroundThread()
        return lastFmRepoTrack.mustFetch(trackId)
    }

    override suspend fun getTrack(trackId: Id): LastFmTrack? {
        assertBackgroundThread()
        val cached = lastFmRepoTrack.getCached(trackId)
        if (cached != null) {
            return cached
        }

        val song = songGateway.getByParam(trackId) ?: return null
        val trackTitle = TextUtils.addSpacesToDash(song.title)
        val trackArtist = if (song.artist == MediaStore.UNKNOWN_STRING) "" else song.artist

        var result: LastFmTrack? = null
        if (song.artist != MediaStore.UNKNOWN_STRING) { // search only if has artist
            result = lastFmService.getTrackInfoAsync(trackTitle, trackArtist).awaitRepeat()?.toDomain(trackId)
        }
        if (result == null) {
            val searchTrack = lastFmService.searchTrackAsync(trackTitle, trackArtist).awaitRepeat()?.toDomain(trackId)
            if (searchTrack != null) {
                result = lastFmService.getTrackInfoAsync(searchTrack.title, searchTrack.artist).awaitRepeat()
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
        return lastFmRepoAlbum.mustFetch(albumId)
    }

    override suspend fun getAlbum(albumId: Id): LastFmAlbum? {
        assertBackgroundThread()
        val album = albumGateway.getByParam(albumId) ?: return null
        if (album.hasSameNameAsFolder) {
            return null
        }

        val cached = lastFmRepoAlbum.getCached(albumId)
        if (cached != null) {
            return cached
        }

        var result : LastFmAlbum? = null
        if (album.title != MediaStore.UNKNOWN_STRING){
            result = lastFmService.getAlbumInfoAsync(album.title, album.artist).awaitRepeat()?.toDomain(albumId)
        }

        if (result == null) {
            val searchAlbum = lastFmService.searchAlbumAsync(album.title).awaitRepeat()?.toDomain(albumId, album.artist)
            if (searchAlbum != null) {
                result = lastFmService.getAlbumInfoAsync(searchAlbum.title, searchAlbum.artist).awaitRepeat()
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
        return lastFmRepoArtist.mustFetch(artistId)
    }

    override suspend fun getArtist(artistId: Id): LastFmArtist? {
        assertBackgroundThread()
        val cached = lastFmRepoArtist.getCached(artistId)
        if (cached != null) {
            return cached
        }
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