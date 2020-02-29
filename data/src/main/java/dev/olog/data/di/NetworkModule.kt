package dev.olog.data.di

import android.content.Context
import com.google.gson.Gson
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dev.olog.data.BuildConfig
import dev.olog.data.api.DeezerService
import dev.olog.data.api.LastFmService
import dev.olog.shared.ApplicationContext
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {

    val gson by lazy { Gson() }

    @Provides
    @JvmStatic
    @Singleton
    internal fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor())
            .addInterceptor(headerInterceptor(context))
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()
    }

    @JvmStatic
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
    @JvmStatic
    @Singleton
    internal fun provideLastFmRetrofit(client: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .callFactory(object: Call.Factory{
                override fun newCall(request: Request): Call {
                    return client.get().newCall(request)
                }
            })
            .build()
    }

    @Provides
    @JvmStatic
    @Singleton
    internal fun provideLastFmRest(retrofit: Retrofit): LastFmService {
        return retrofit.create(LastFmService::class.java)
    }

    @Provides
    @JvmStatic
    @Singleton
    internal fun provideDeezerRest(retrofit: Retrofit): DeezerService {
        val newBuilder = retrofit.newBuilder()
            .baseUrl("https://api.deezer.com/")
            .build()
        return newBuilder.create(DeezerService::class.java)
    }

}