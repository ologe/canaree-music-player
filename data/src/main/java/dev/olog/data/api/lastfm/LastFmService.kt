package dev.olog.data.api.lastfm

import androidx.annotation.IntRange
import dev.olog.data.api.lastfm.album.AlbumInfo
import dev.olog.data.api.lastfm.album.AlbumSearch
import dev.olog.data.api.lastfm.artist.ArtistInfo
import dev.olog.data.api.lastfm.artist.ArtistSearch
import dev.olog.data.api.lastfm.track.TrackInfo
import dev.olog.data.api.lastfm.track.TrackSearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val MIN_SEARCH_PAGES = 1L
private const val MAX_SEARCH_PAGES = 5L
private const val DEFAULT_SEARCH_PAGES = 5L

interface LastFmService {

    @GET("?method=track.getInfo")
    suspend fun getTrackInfoAsync(
            @Query("track", encoded = true) track: String,
            @Query("artist", encoded = true) artist: String,
    ) : Response<TrackInfo>

    @GET("?method=track.search")
    suspend fun searchTrackAsync(
            @Query("track", encoded = true) track: String,
            @Query("artist", encoded = true) artist: String = "",
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<TrackSearch>

    @GET("?method=artist.getinfo")
    suspend fun getArtistInfoAsync(
        @Query("artist", encoded = true) artist: String,
        @Query("lang") language: String = "en"
    ): Response<ArtistInfo>

    @GET("?method=artist.search")
    suspend fun searchArtistAsync(
            @Query("artist", encoded = true) artist: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<ArtistSearch>

    @GET("?method=album.getinfo")
    suspend fun getAlbumInfoAsync(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
        @Query("lang") language: String = "en"
    ): Response<AlbumInfo>

    @GET("?method=album.search")
    suspend fun searchAlbumAsync(
            @Query("album", encoded = true) album: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<AlbumSearch>

}