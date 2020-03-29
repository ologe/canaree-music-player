package dev.olog.data.spotify.service

import dev.olog.data.shared.retrofit.IoResult
import dev.olog.data.spotify.BuildConfig
import dev.olog.data.spotify.entity.SpotifyToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface SpotifyLoginService {

    @Headers("Authorization: Basic ${BuildConfig.SPOTIFY_ENCODED_CLIENT}")
    @FormUrlEncoded
    @POST("api/token")
    suspend fun getToken(
        @Field("grant_type") grantType: String = "client_credentials"
    ): IoResult<SpotifyToken>

}