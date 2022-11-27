package dev.olog.data.api.lastfm

import androidx.annotation.IntRange
import dev.olog.core.IoResult
import dev.olog.data.api.lastfm.album.LastFmAlbumInfoDto
import dev.olog.data.api.lastfm.album.LastFmAlbumSearchDto
import dev.olog.data.api.lastfm.artist.LastFmArtistInfoDto
import dev.olog.data.api.lastfm.track.LastFmTrackInfoDto
import dev.olog.data.api.lastfm.track.LastFmTrackSearchDto
import retrofit2.http.GET
import retrofit2.http.Query

private const val MIN_SEARCH_PAGES = 1L
private const val MAX_SEARCH_PAGES = 5L
private const val DEFAULT_SEARCH_PAGES = 5L

interface LastFmService {

    @GET("?method=track.getInfo")
    suspend fun getTrackInfo(
            @Query("track") track: String,
            @Query("artist") artist: String,
    ) : IoResult<LastFmTrackInfoDto>

    @GET("?method=track.search")
    suspend fun searchTrack(
            @Query("track") track: String,
            @Query("artist") artist: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): IoResult<LastFmTrackSearchDto>

    @GET("?method=artist.getinfo")
    suspend fun getArtistInfo(
        @Query("artist") artist: String,
    ): IoResult<LastFmArtistInfoDto>

    @GET("?method=album.getinfo")
    suspend fun getAlbumInfo(
        @Query("album") album: String,
        @Query("artist") artist: String,
    ): IoResult<LastFmAlbumInfoDto>

    @GET("?method=album.search")
    suspend fun searchAlbum(
            @Query("album") album: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): IoResult<LastFmAlbumSearchDto>

}