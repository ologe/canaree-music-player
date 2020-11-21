package dev.olog.data.api.deezer

import dev.olog.data.api.deezer.entity.DeezerAlbumSearchResultDto
import dev.olog.data.api.deezer.entity.DeezerArtistSearchResultDto
import dev.olog.data.api.deezer.entity.DeezerTrackSearchResultDto
import dev.olog.lib.network.model.IoResult
import retrofit2.http.GET
import retrofit2.http.Query

internal interface DeezerService {

    @GET("search/artist&limit=1")
    suspend fun getArtist(
        @Query("q") artistName: String
    ): IoResult<DeezerArtistSearchResultDto>

    @GET("search/track&limit=1")
    suspend fun getTrack(
        @Query("q") trackName: String
    ): IoResult<DeezerTrackSearchResultDto>

    @GET("search/album&limit=1")
    suspend fun getAlbum(
        @Query("q") trackName: String
    ): IoResult<DeezerAlbumSearchResultDto>

}