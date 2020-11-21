package dev.olog.lib.network

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.lib.network.retrofit.IoResultAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal object SharedNetworkModule {

    @Provides
    fun provideCallAdapterFactory(connectivityManager: ConnectivityManager): CallAdapter.Factory {
        return IoResultAdapterFactory(connectivityManager, Dispatchers.IO)
    }

    @Provides
    @Singleton
    internal fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(logInterceptor())
            .addInterceptor(headerInterceptor(context))
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
            val request = original.newBuilder()
                .header("User-Agent", context.packageName)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(original.method, original.body)
                .build()
            it.proceed(request)
        }
    }

}