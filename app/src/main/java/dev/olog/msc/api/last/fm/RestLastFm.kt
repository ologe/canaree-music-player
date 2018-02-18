package dev.olog.msc.api.last.fm

import android.support.annotation.IntRange
import dev.olog.msc.api.last.fm.album.info.AlbumInfo
import dev.olog.msc.api.last.fm.album.search.AlbumSearch
import dev.olog.msc.api.last.fm.artist.info.ArtistInfo
import dev.olog.msc.api.last.fm.artist.search.ArtistSearch
import dev.olog.msc.api.last.fm.track.info.TrackInfo
import dev.olog.msc.api.last.fm.track.search.TrackSearch
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "56553f687cba2aa671c99caff536def1"
private const val MIN_SEARCH_PAGES = 1L
private const val MAX_SEARCH_PAGES = 5L
private const val DEFAULT_SEARCH_PAGES = MAX_SEARCH_PAGES

private const val DEFAULT_AUTO_CORRECT = 1L

private const val BASE_URL = "?api_key=$API_KEY&format=json"

interface RestLastFm {

//    https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes
    // 639-1 works, 639-2 not works

    @GET("$BASE_URL&method=track.getInfo")
    fun getTrackInfo(
            @Query("track", encoded = true) track: String,
            @Query("artist", encoded = true) artist: String,
            @IntRange(from = 0, to = 1) @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT
    ) : Single<TrackInfo>

    @GET("$BASE_URL&method=track.search")
    fun searchTrack(
            @Query("track", encoded = true) track: String,
            @Query("artist", encoded = true) artist: String = "",
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Single<TrackSearch>

    @GET("$BASE_URL&method=artist.getinfo")
    fun getArtistInfo(
            @Query("artist", encoded = true) artist: String,
            @IntRange(from = 0, to = 1)
            @Query("autocorrect") autocorrect: Long = DEFAULT_AUTO_CORRECT,
            @Query("lang") language: String = "en"
    ): Single<ArtistInfo>

    @GET("$BASE_URL&method=artist.search")
    fun searchArtist(
            @Query("artist", encoded = true) artist: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Single<ArtistSearch>

    @GET("$BASE_URL&method=album.getinfo")
    fun getAlbumInfo(
            @Query("album", encoded = true) album: String,
            @Query("artist", encoded = true) artist: String,
            @IntRange(from = 0, to = 1)
            @Query("autocorrect") autocorrect: Long= DEFAULT_AUTO_CORRECT,
            @Query("lang") language: String = "en"
    ): Single<AlbumInfo>

    @GET("$BASE_URL&method=album.search")
    fun searchAlbum(
            @Query("album", encoded = true) album: String,
            @IntRange(from = MIN_SEARCH_PAGES, to = MAX_SEARCH_PAGES)
            @Query("limit") limit: Long = DEFAULT_SEARCH_PAGES
    ): Single<AlbumSearch>

}