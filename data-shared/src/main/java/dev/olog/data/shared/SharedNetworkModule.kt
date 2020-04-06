package dev.olog.data.shared

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dev.olog.data.shared.retrofit.adapter.IoResultCallAdapterFactory
import dev.olog.core.ApplicationContext
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object SharedNetworkModule {

    val gson by lazy { Gson() }

    @Provides
    @Singleton
    internal fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    internal fun provideResultCallAdapterFactory(): CallAdapter.Factory {
        return IoResultCallAdapterFactory(Dispatchers.IO)
    }

    @Provides
    @Singleton
    internal fun provideOkHttp(@dev.olog.core.ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor())
            .addInterceptor(headerInterceptor(context))
            .also {
                if (BuildConfig.DEBUG) {
                    it.addInterceptor(ChuckerInterceptor(context))
                }
            }
            .build()
    }

    private fun logInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // disable retrofit log on release
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    private fun headerInterceptor(context: Context): Interceptor {
        return Interceptor {
            val original = it.request()
            val request = it.request().newBuilder()
                .header("User-Agent", context.packageName)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(original.method, original.body)
                .build()
            it.proceed(request)
        }
    }

}