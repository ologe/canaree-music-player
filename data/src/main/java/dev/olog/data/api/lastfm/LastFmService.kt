package dev.olog.data.api.lastfm

import dev.olog.data.api.lastfm.album.AlbumInfo
import dev.olog.data.api.lastfm.album.AlbumSearch
import dev.olog.data.api.lastfm.artist.ArtistInfo
import dev.olog.data.api.lastfm.artist.ArtistSearch
import dev.olog.data.api.lastfm.track.TrackInfo
import dev.olog.data.api.lastfm.track.TrackSearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

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
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<TrackSearch>

    @GET("?method=artist.getinfo")
    suspend fun getArtistInfoAsync(
            @Query("artist", encoded = true) artist: String,
    ): Response<ArtistInfo>

    @GET("?method=artist.search")
    suspend fun searchArtistAsync(
            @Query("artist", encoded = true) artist: String,
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<ArtistSearch>

    @GET("?method=album.getinfo")
    suspend fun getAlbumInfoAsync(
            @Query("album", encoded = true) album: String,
            @Query("artist", encoded = true) artist: String,
    ): Response<AlbumInfo>

    @GET("?method=album.search")
    suspend fun searchAlbumAsync(
            @Query("album", encoded = true) album: String,
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Response<AlbumSearch>

}