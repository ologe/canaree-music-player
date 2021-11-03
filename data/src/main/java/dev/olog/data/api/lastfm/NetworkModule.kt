package dev.olog.data.api.lastfm

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.Config
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.dagger.ApplicationInterceptor
import dev.olog.data.dagger.NetworkInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttp(
        @ApplicationInterceptor applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
        @NetworkInterceptor networkInterceptors: Set<@JvmSuppressWildcards Interceptor>,
        config: Config,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()

        applicationInterceptors.forEach { builder.addInterceptor(it) }
        networkInterceptors.forEach { builder.addNetworkInterceptor(it) }

        if (config.isDebug) {
            builder.addNetworkInterceptor(logInterceptor())
        }

        return builder.build()
    }

    private fun logInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @IntoSet
    @ApplicationInterceptor
    @Singleton
    fun provideHeaderInterceptor(@ApplicationContext context: Context): Interceptor {
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