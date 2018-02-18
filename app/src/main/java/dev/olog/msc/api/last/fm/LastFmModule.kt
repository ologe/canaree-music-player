package dev.olog.msc.api.last.fm

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.BuildConfig
import dev.olog.msc.dagger.qualifier.ApplicationContext
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class LogInterceptor

@Qualifier
annotation class HeaderInterceptor

@Module
class LastFmModule {

    @Provides
    @LogInterceptor
    fun provideInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG){
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            // disable retrofit log on release
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    @Provides
    @HeaderInterceptor
    fun provideHeaderInterceptor(@ApplicationContext context: Context): Interceptor{
        return Interceptor {
            val original = it.request()
            val request = original.newBuilder()
                    .header("User-Agent", context.packageName)
                    .method(original.method(), original.body())
                    .build()
            it.proceed(request)
        }
    }

    @Provides
    fun provideLogInterceptor(
            @LogInterceptor logInterceptor: Interceptor,
            @HeaderInterceptor headerInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addNetworkInterceptor(logInterceptor)
                .addInterceptor(headerInterceptor)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun provideLastFmRest(retrofit: Retrofit): RestLastFm {
        return retrofit.create(RestLastFm::class.java)
    }


}