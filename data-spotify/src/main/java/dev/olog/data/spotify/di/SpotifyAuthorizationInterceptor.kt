package dev.olog.data.spotify.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.data.spotify.gateway.SpotifyLoginRepository
import dev.olog.shared.android.utils.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

internal class SpotifyAuthorizationInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginRepository: SpotifyLoginRepository
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        if (!loginRepository.isTokenValid()) {
            // token was never set, fetch it and update headers
            val newToken = loginRepository.acquireToken()
            val builder = chain.request().newBuilder()
                .setBearerToken(newToken)
                .header("Authorization", "Bearer $newToken")
            return chain.proceed(builder.build())
        }
        // current token is non null
        val currentToken = loginRepository.getToken()

        val currentRequest = chain.request().newBuilder()
            .setBearerToken(currentToken)
            .build()

        if (!NetworkUtils.isConnected(context)) {
            // try from cached, TODO not working, call are failing
            return chain.proceed(currentRequest)
        }

        val mainResponse = chain.proceed(currentRequest)

        if (mainResponse.code == 401 || mainResponse.code == 403) {
            // token expired, get new token
            val newToken = loginRepository.acquireToken()

            val builder = currentRequest.newBuilder()
                .setBearerToken(newToken)
                .method(currentRequest.method, currentRequest.body)
            return chain.proceed(builder.build())
        } else {
            return chain.proceed(currentRequest)
        }
    }

    private fun Request.Builder.setBearerToken(token: String): Request.Builder {
        return this.header("Authorization", "Bearer $token")
    }

}