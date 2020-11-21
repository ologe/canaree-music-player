package dev.olog.data.api.lastfm

import androidx.annotation.IntRange
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Album
import dev.olog.data.BuildConfig
import dev.olog.data.api.lastfm.album.LastFmAlbumInfoDto
import dev.olog.data.api.lastfm.album.LastFmAlbumSearchDto
import dev.olog.data.api.lastfm.album.toDomain
import dev.olog.data.api.lastfm.artist.LastFmArtistInfoDto
import dev.olog.data.api.lastfm.track.LastFmTrackInfoDto
import dev.olog.data.api.lastfm.track.LastFmTrackSearchDto
import dev.olog.data.api.lastfm.track.toDomain
import dev.olog.lib.network.model.IoResult
import dev.olog.lib.network.model.getOrNull
import retrofit2.http.GET
import retrofit2.http.Query

internal interface LastFmService {

    companion object {
        // TODO hardcode values in the url since they never changes
        private const val MIN_SEARCH_PAGES = 1L
        private const val MAX_SEARCH_PAGES = 5L
        private const val DEFAULT_SEARCH_PAGES = MAX_SEARCH_PAGES

        private const val DEFAULT_AUTO_CORRECT = 1L

        // TODO move to retrofit base url?
        private const val BASE_URL = "?api_key=${BuildConfig.LAST_FM_KEY}&format=json"
    }

    @GET("$BASE_URL&method=track.getInfo")
    suspend fun getTrackInfoInternal(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1) @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT
    ): IoResult<LastFmTrackInfoDto>

    @GET("$BASE_URL&method=track.search")
    suspend fun searchTrackInternal(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String = "",
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): IoResult<LastFmTrackSearchDto>

    @GET("$BASE_URL&method=artist.getinfo")
    suspend fun getArtistInfo(
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): IoResult<LastFmArtistInfoDto>

    @GET("$BASE_URL&method=album.getinfo")
    suspend fun getAlbumInfoInternal(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): IoResult<LastFmAlbumInfoDto>

    @GET("$BASE_URL&method=album.search")
    suspend fun searchAlbumInternal(
        @Query("album", encoded = true) album: String,
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): IoResult<LastFmAlbumSearchDto>


    suspend fun getAlbumInfo(album: Album): LastFmAlbum? {
        return getAlbumInfoInternal(album = album.title, artist = album.artist)
            .getOrNull()
            ?.toDomain(album.id)
    }

    suspend fun searchAlbum(album: Album): LastFmAlbum? {
        val result = searchAlbumInternal(album.title).getOrNull()?.toDomain(album.id, album.artist)

        if (result != null && result.title.isNotBlank() && result.artist.isNotBlank()) {
            return getAlbumInfoInternal(result.title, result.artist).getOrNull()?.toDomain(album.id)
        }
        return null
    }

    suspend fun getTrackInfo(
        id: Long,
        title: String,
        artist: String,
    ): LastFmTrack? {
        return getTrackInfoInternal(track = title, artist = artist)
            .getOrNull()
            ?.toDomain(id)
    }

    suspend fun searchTrack(
        id: Long,
        title: String,
        artist: String,
    ): LastFmTrack? {
        val result = searchTrackInternal(title).getOrNull()?.toDomain(id)

        if (result != null && result.title.isNotBlank() && result.artist.isNotBlank()) {
            return getTrackInfoInternal(result.title, result.artist)?.getOrNull()?.toDomain(id)
        }
        return null
    }

}