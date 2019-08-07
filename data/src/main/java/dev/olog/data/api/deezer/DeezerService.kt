package dev.olog.data.api.deezer

import dev.olog.data.api.deezer.artist.DeezerArtistResponse
import dev.olog.data.api.deezer.artist.DeezerTrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") artistName: String
    ): Response<DeezerArtistResponse>

    @GET("search/track&limit=1")
    suspend fun getTrack(
        @Query("q") trackName: String
    ): Response<DeezerTrackResponse>

}