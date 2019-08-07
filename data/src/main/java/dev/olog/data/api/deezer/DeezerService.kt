package dev.olog.data.api.deezer

import dev.olog.data.api.deezer.artist.DeezerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") artistName: String
    ): Response<DeezerResponse>

}