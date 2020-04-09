package dev.olog.data.api

import androidx.annotation.IntRange
import dev.olog.data.BuildConfig
import dev.olog.data.model.lastfm.*
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
    suspend fun getTrackInfoAsync(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1) @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT
    ): Response<LastFmTrackInfo>

    @GET("$BASE_URL&method=track.search")
    suspend fun searchTrackAsync(
        @Query("track", encoded = true) track: String,
        @Query("artist", encoded = true) artist: String = "",
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<LastFmTrackSearch>

    @GET("$BASE_URL&method=artist.getinfo")
    suspend fun getArtistInfoAsync(
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): Response<LastFmArtistInfo>

    @GET("$BASE_URL&method=album.getinfo")
    suspend fun getAlbumInfoAsync(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
        @IntRange(from = 0, to = 1)
        @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
        @Query("lang") language: String = "en"
    ): Response<LastFmAlbumInfo>

    @GET("$BASE_URL&method=album.search")
    suspend fun searchAlbumAsync(
        @Query("album", encoded = true) album: String
    ): Response<LastFmAlbumSearch>

}