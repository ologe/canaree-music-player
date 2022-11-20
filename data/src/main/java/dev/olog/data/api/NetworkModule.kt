package dev.olog.data.api

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.data.api.deezer.DeezerService
import dev.olog.data.api.lastfm.LastFmService
import dev.olog.data.network.LastFmInterceptor
import dev.olog.data.network.IoResultCallAdapter
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(
            directory = File(context.cacheDir, "okhttp"),
            maxSize = 100L * 1024L * 1024L, // 100 MB
        )
    }

    @Provides
    @Singleton
    internal fun provideOkHttp(
        config: Config,
        lastFmInterceptor: LastFmInterceptor,
        cache: Cache,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor(config))
            .addInterceptor(lastFmInterceptor)
            .cache(cache)
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
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(config.lastFmBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(IoResultCallAdapter.Factory())
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