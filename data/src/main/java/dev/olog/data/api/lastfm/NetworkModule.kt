package dev.olog.data.api.lastfm

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.data.api.deezer.DeezerService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideOkHttp(
        config: Config,
        lastFmInterceptor: LastFmInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor(config))
            .addInterceptor(lastFmInterceptor)
            .build()
    }

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

    @Provides
    @Singleton
    internal fun provideLastFmRetrofit(
        client: OkHttpClient,
        config: Config,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.lastFmBaseUrl)
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