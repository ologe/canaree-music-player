package dev.olog.lib.network

import dagger.Lazy
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

fun Retrofit.Builder.withLazyCallFactory(client: Lazy<OkHttpClient>): Retrofit.Builder {
    return this.callFactory(object : Call.Factory {
        override fun newCall(request: Request): Call {
            return client.get().newCall(request)
        }
    })
}