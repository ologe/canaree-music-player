package dev.olog.data.api

import androidx.annotation.IntRange
import dev.olog.data.BuildConfig
import dev.olog.data.model.lastfm.*
import dev.olog.lib.network.retrofit.IoResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmService {

    companion object {
        private const val MIN_SEARCH_PAGES = 1L
        private const val MAX_SEARCH_PAGES = 5L
        private const val DEFAULT_SEARCH_PAGES = MAX_SEARCH_PAGES

        private const val DEFAULT_AUTO_CORRECT = 1L

        private const val BASE_URL = "?api_key=${BuildConfig.LAST_FM_KEY}&format=json"
    }

    @GET("$BASE_URL&method=track.getInfo")
    suspend fun getTrackInfo(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1) @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT
    ): IoResult<LastFmTrackInfo>

    @GET("$BASE_URL&method=track.search")
    suspend fun searchTrack(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String = "",
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): IoResult<LastFmTrackSearch>

    @GET("$BASE_URL&method=artist.getinfo")
    suspend fun getArtistInfo(
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): IoResult<LastFmArtistInfo>

    @GET("$BASE_URL&method=album.getinfo")
    suspend fun getAlbumInfo(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): IoResult<LastFmAlbumInfo>

    @GET("$BASE_URL&method=album.search")
    suspend fun searchAlbum(
        @Query("album", encoded = true) album: String
    ): IoResult<LastFmAlbumSearch>

}