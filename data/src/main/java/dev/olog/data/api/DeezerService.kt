package dev.olog.data.api

import dev.olog.data.model.deezer.DeezerAlbumResponse
import dev.olog.data.model.deezer.DeezerArtistResponse
import dev.olog.data.model.deezer.DeezerTrackResponse
import dev.olog.lib.network.retrofit.IoResult
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") artistName: String
    ): IoResult<DeezerArtistResponse>

    @GET("search/track&limit=1")
    suspend fun getTrack(
        @Query("q") trackName: String
    ): IoResult<DeezerTrackResponse>

    @GET("search/album&limit=1")
    suspend fun getAlbum(
        @Query("q") trackName: String
    ): IoResult<DeezerAlbumResponse>

}