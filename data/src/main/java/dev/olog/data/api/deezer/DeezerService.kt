package dev.olog.data.api.deezer

import dev.olog.core.IoResult
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") query: String
    ): IoResult<DeezerArtistResponse>

    @GET("search/track&limit=1")
    suspend fun getTrack(
        @Query("q") query: String
    ): IoResult<DeezerTrackResponse>

    @GET("search/album&limit=1")
    suspend fun getAlbum(
        @Query("q") query: String
    ): IoResult<DeezerAlbumResponse>

}