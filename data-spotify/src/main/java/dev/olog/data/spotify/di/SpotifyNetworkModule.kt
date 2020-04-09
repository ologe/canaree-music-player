package dev.olog.data.spotify.di

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.lib.network.SharedNetworkModule
import dev.olog.lib.network.withLazyCallFactory
import dev.olog.data.spotify.gateway.SpotifyGatewayImpl
import dev.olog.data.spotify.service.SpotifyLoginService
import dev.olog.data.spotify.service.SpotifyService
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

private const val SPOTIFY_API = "https://api.spotify.com/v1/"
private const val SPOTIFY_ACCOUNTS = "https://accounts.spotify.com/"

@Module(includes = [SharedNetworkModule::class])
abstract class SpotifyNetworkModule {

    @Binds
    @Singleton
    internal abstract fun provideSpotifyRepository(repository: SpotifyGatewayImpl): SpotifyGateway

    companion object {

        @Provides
        @Singleton
        internal fun provideSpotifyService(
            converterFactory: Converter.Factory,
            callAdapter: CallAdapter.Factory,
            client: Lazy<OkHttpClient>,
            spotifyAuthorizationInterceptor: SpotifyAuthorizationInterceptor
        ): SpotifyService {

            val lazy = Lazy {
                client.get().newBuilder()
                    .addInterceptor(spotifyAuthorizationInterceptor)
                    .build()
            }

            return Retrofit.Builder()
                .baseUrl(SPOTIFY_API)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapter)
                .withLazyCallFactory(lazy)
                .build()
                .create(SpotifyService::class.java)
        }

        @Provides
        @Singleton
        internal fun provideSpotifyLoginService(
            converterFactory: Converter.Factory,
            callAdapter: CallAdapter.Factory,
            client: Lazy<OkHttpClient>
        ): SpotifyLoginService {
            return Retrofit.Builder()
                .baseUrl(SPOTIFY_ACCOUNTS)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapter)
                .withLazyCallFactory(client)
                .build()
                .create(SpotifyLoginService::class.java)
        }
    }

}