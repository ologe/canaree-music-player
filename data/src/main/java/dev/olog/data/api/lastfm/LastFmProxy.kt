package dev.olog.data.api.lastfm

import android.provider.MediaStore
import dev.olog.data.api.lastfm.album.info.AlbumInfo
import dev.olog.data.api.lastfm.album.search.AlbumSearch
import dev.olog.data.api.lastfm.annotation.Impl
import dev.olog.data.api.lastfm.artist.info.ArtistInfo
import dev.olog.data.api.lastfm.artist.search.ArtistSearch
import dev.olog.data.api.lastfm.track.info.TrackInfo
import dev.olog.data.api.lastfm.track.search.TrackSearch
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.net.URLEncoder
import javax.inject.Inject

class LastFmProxy @Inject constructor(
        @Impl private val impl: LastFmService

): LastFmService {

    /**
     * [https://www.last.fm/api/show/track.getInfo]
     * A not unknown artist is required
     */
    override fun getTrackInfoAsync(track: String, artist: String, autocorrect: Long): Deferred<Response<TrackInfo>> {
        if (artist == MediaStore.UNKNOWN_STRING){
            throw IllegalArgumentException("artist can not be unknown")
        }

        val normalizedTrack = UTF8NormalizedEntity(track)
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getTrackInfoAsync(
                normalizedTrack.value,
                normalizedArtist.value
        )
    }

    override fun searchTrackAsync(track: String, artist: String, limit: Long): Deferred<Response<TrackSearch>> {
        val normalizedTrack = UTF8NormalizedEntity(track)
        val normalizedArtist =
            UTF8NormalizedEntity(if (artist == MediaStore.UNKNOWN_STRING) "" else artist)
        return impl.searchTrackAsync(
                normalizedTrack.value,
                normalizedArtist.value
        )
    }

    override fun getArtistInfoAsync(artist: String, autocorrect: Long, language: String): Deferred<Response<ArtistInfo>> {
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getArtistInfoAsync(
                normalizedArtist.value
        )
    }

    override fun searchArtistAsync(artist: String, limit: Long): Deferred<Response<ArtistSearch>> {
        val normalizedArtist = UTF8NormalizedEntity(artist).value
        return impl.searchArtistAsync(normalizedArtist)
    }

    /**
     * [https://www.last.fm/api/show/album.getInfo]
     * A not unknown artist is required
     */
    override fun getAlbumInfoAsync(album: String, artist: String, autocorrect: Long, language: String): Deferred<Response<AlbumInfo>> {
        if (artist == MediaStore.UNKNOWN_STRING){
            throw IllegalArgumentException("artist can not be unknown")
        }

        val normalizedAlbum = UTF8NormalizedEntity(album)
        val normalizedArtist = UTF8NormalizedEntity(artist)
        return impl.getAlbumInfoAsync(
                normalizedAlbum.value,
                normalizedArtist.value
        )
    }

    override fun searchAlbumAsync(album: String, limit: Long): Deferred<Response<AlbumSearch>> {
        val normalizedAlbum = UTF8NormalizedEntity(album).value
        return impl.searchAlbumAsync(normalizedAlbum)
    }

    private class UTF8NormalizedEntity(value: String) {
        val value : String = URLEncoder.encode(value, "UTF-8")
    }

}