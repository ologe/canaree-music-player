package dev.olog.data.api.lastfm

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.data.api.deezer.DeezerService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttp(
        @ApplicationContext context: Context,
        config: Config,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor(config))
            .addInterceptor(headerInterceptor(context))
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()
    }

    @JvmStatic
    private fun logInterceptor(config: Config): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (config.isDebug) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // disable retrofit log on release
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    @JvmStatic
    private fun headerInterceptor(context: Context): Interceptor {
        return Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                .header("User-Agent", context.packageName)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(original.method, original.body)
                .build()
            it.proceed(request)
        }
    }

    @Provides
    @Singleton
    internal fun provideLastFmRetrofit(
        client: OkHttpClient,
        config: Config,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/?api_key=${config.lastFmKey}&format=json&")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideLastFmRest(retrofit: Retrofit): LastFmService {
        return retrofit.create(LastFmService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDeezerRest(retrofit: Retrofit): DeezerService {
        val newBuilder = retrofit.newBuilder()
            .baseUrl("https://api.deezer.com/")
            .build()
        return newBuilder.create(DeezerService::class.java)
    }

}