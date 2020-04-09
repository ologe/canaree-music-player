package dev.olog.data.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.data.api.DeezerService
import dev.olog.data.api.LastFmService
import dev.olog.data.spotify.di.SpotifyNetworkModule
import dev.olog.lib.network.SharedNetworkModule
import dev.olog.lib.network.withLazyCallFactory
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

private const val LAST_FM = "http://ws.audioscrobbler.com/2.0/"
private const val DEEZER = "https://api.deezer.com/"

@Module(
    includes = [
        SpotifyNetworkModule::class,
        SharedNetworkModule::class
    ]
)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideLastFmRest(
        converterFactory: Converter.Factory,
        callAdapter: CallAdapter.Factory,
        client: Lazy<OkHttpClient>
    ): LastFmService {
        return Retrofit.Builder()
            .baseUrl(LAST_FM)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapter)
            .withLazyCallFactory(client)
            .build()
            .create(LastFmService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDeezerRest(
        converterFactory: Converter.Factory,
        callAdapter: CallAdapter.Factory,
        client: Lazy<OkHttpClient>
    ): DeezerService {
        return Retrofit.Builder()
            .baseUrl(DEEZER)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapter)
            .withLazyCallFactory(client)
            .build()
            .create(DeezerService::class.java)
    }

}