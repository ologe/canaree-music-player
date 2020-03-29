package dev.olog.data.spotify.di

import dev.olog.data.spotify.gateway.SpotifyLoginRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

internal class SpotifyAuthorizationInterceptor @Inject constructor(
    private val loginRepository: SpotifyLoginRepository
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        if (!loginRepository.isTokenValid()) {
            // token was never set, fetch it and update headers
            val newToken = loginRepository.acquireToken()
            val builder = chain.request()
                .newBuilder()
                .setBearerToken(newToken)
                .header("Authorization", "Bearer $newToken")
            return chain.proceed(builder.build())
        }
        // current token is non null
        val currentToken = loginRepository.getToken()

        val currentRequest = chain.request().newBuilder()
            .setBearerToken(currentToken)
            .build()

        val mainResponse = chain.proceed(currentRequest)
        val mainRequest = chain.request()

        if (mainResponse.code == 401 || mainResponse.code == 403) {
            // token expired, get new token
            val newToken = loginRepository.acquireToken()

            val builder = mainRequest.newBuilder()
                .setBearerToken(newToken)
                .method(mainRequest.method, mainRequest.body)
            return chain.proceed(builder.build())
        } else {
            // token is valid
            val token = loginRepository.getToken()
            val builder = mainRequest.newBuilder()
                .setBearerToken(token)
                .method(mainRequest.method, mainRequest.body)
            return chain.proceed(builder.build())
        }
    }

    private fun Request.Builder.setBearerToken(token: String): Request.Builder {
        return this.header("Authorization", "Bearer $token")
    }

}