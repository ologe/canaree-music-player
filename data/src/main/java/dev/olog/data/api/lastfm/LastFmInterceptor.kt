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
        val original = chain.request()
        val url = original.url

        val newUrl = if (url.toString().contains("ws.audioscrobbler.com")) {
            original.url.newBuilder()
                .addQueryParameter("api_key", config.lastFmKey)
                .addQueryParameter("format", "json")
                .addQueryParameter("lang", "en") // TODO localize?
                .addQueryParameter("autocorrect", "1")
                .build()
        } else url


        val request = original.newBuilder()
            .url(newUrl)
            .header("User-Agent", context.packageName)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
        return chain.proceed(request)
    }
}