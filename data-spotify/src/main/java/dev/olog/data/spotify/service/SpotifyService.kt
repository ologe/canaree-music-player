package dev.olog.data.spotify.service

import dev.olog.data.shared.retrofit.IoResult
import dev.olog.data.spotify.entity.SpotifyArtistAlbumItems
import dev.olog.data.spotify.entity.SpotifySearchArtistResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyService {

    @GET("search?offset=0&limit=5&type=artist")
    suspend fun searchArtist(
        @Query("q") query: String
    ): IoResult<SpotifySearchArtistResult>

    @GET("artists/{id}/albums?include_groups=album")
    suspend fun searchArtistAlbums(
        @Path("id") artistId: String
    ): IoResult<SpotifyArtistAlbumItems>

}



