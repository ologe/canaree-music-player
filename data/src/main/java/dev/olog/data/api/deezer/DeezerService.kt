package dev.olog.data.api.deezer

import dev.olog.core.Try
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") artistName: String
    ): Try<DeezerArtistResponse>

    @GET("search/track&limit=1")
    suspend fun getTrack(
        @Query("q") trackName: String
    ): Try<DeezerTrackResponse>

    @GET("search/album&limit=1")
    suspend fun getAlbum(
        @Query("q") trackName: String
    ): Try<DeezerAlbumResponse>

}