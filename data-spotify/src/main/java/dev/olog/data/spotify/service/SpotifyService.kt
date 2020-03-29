package dev.olog.data.spotify.service

import dev.olog.data.shared.retrofit.IoResult
import dev.olog.data.spotify.entity.complex.RemoteSpotifyArtistAlbum
import dev.olog.data.spotify.entity.complex.RemoteSpotifyArtistTopTracks
import dev.olog.data.spotify.entity.complex.RemoteSpotifySearchArtist
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyService {

    @GET("search?offset=0&limit=10&type=artist&market=US")
    suspend fun searchArtist(
        @Query("q") query: String
    ): IoResult<RemoteSpotifySearchArtist>

    @GET("artists/{id}/albums?&country=US&limit=50")
    suspend fun getArtistAlbums(
        @Path("id") artistId: String,
        @Query("include_groups") type: String
    ): IoResult<RemoteSpotifyArtistAlbum>

    @GET("artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: String
    ): IoResult<RemoteSpotifyArtistTopTracks>

}



