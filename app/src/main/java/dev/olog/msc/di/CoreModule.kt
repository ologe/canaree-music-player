package dev.olog.msc.di

import android.app.Application
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.core.IEncrypter
import dev.olog.msc.BuildConfig
import dev.olog.msc.encryption.EncrypterImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    companion object {

        @Provides
        internal fun provideResources(instance: Application): Resources = instance.resources

        @Provides
        internal fun provideContentResolver(instance: Application): ContentResolver {
            return instance.contentResolver
        }

        @Provides
        fun provideConnectivityManager(instance: Application): ConnectivityManager {
            return instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        @Provides
        internal fun provideNotificationManager(instance: Application): NotificationManager {
            return instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        fun provideConfig(): Config {
            return Config(
                isDebug = BuildConfig.DEBUG,
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                lastFmBaseUrl = "http://ws.audioscrobbler.com/2.0/",
                lastFmKey = BuildConfig.LAST_FM_KEY,
                lastFmSecret = BuildConfig.LAST_FM_SECRET,
                deezerBaseUrl = "https://api.deezer.com/",
                aesPassword = BuildConfig.AES_PASSWORD,
            )
        }

    }

}