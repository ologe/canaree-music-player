package dev.olog.data.api.lastfm

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.Config
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LastFmInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: Config,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        if (!originalUrl.toString().startsWith(config.lastFmBaseUrl)) {
            // not a request to last fm api
            return chain.proceed(originalRequest)
        }

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("api_key", config.lastFmKey)
            .addQueryParameter("format", "json")
            .addQueryParameter("autocorrect", "1")
            .addQueryParameter("lang", "en") // todo use device language?
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .addHeader("User-Agent", context.packageName)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        return chain.proceed(newRequest)
    }

}