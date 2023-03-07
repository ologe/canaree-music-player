package dev.olog.data.api.lastfm

import androidx.annotation.IntRange
import dev.olog.core.Try
import dev.olog.data.api.lastfm.album.AlbumInfo
import dev.olog.data.api.lastfm.album.AlbumSearch
import dev.olog.data.api.lastfm.artist.ArtistInfo
import dev.olog.data.api.lastfm.track.TrackInfo
import dev.olog.data.api.lastfm.track.TrackSearch
import retrofit2.http.GET
import retrofit2.http.Query

private const val MIN_SEARCH_PAGES = 1L
private const val MAX_SEARCH_PAGES = 5L
private const val DEFAULT_SEARCH_PAGES = MAX_SEARCH_PAGES

interface LastFmService {

    @GET("?method=track.getInfo")
    suspend fun getTrackInfo(
        @Query("track") track: String,
        @Query("artist") artist: String,
    ) : Try<TrackInfo>

    @GET("?method=track.search")
    suspend fun searchTrack(
        @Query("track") track: String,
        @Query("artist") artist: String = "",
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Try<TrackSearch>

    @GET("?method=artist.getinfo")
    suspend fun getArtistInfo(
        @Query("artist") artist: String,
    ): Try<ArtistInfo>

    @GET("?method=album.getinfo")
    suspend fun getAlbumInfo(
        @Query("album", encoded = true) album: String,
        @Query("artist", encoded = true) artist: String,
    ): Try<AlbumInfo>

    @GET("?method=album.search")
    suspend fun searchAlbum(
        @Query("album", encoded = true) album: String,
        @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
        @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Try<AlbumSearch>

}