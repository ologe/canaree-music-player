package dev.olog.msc.app

import android.app.Application
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
import dev.olog.injection.EncrypterImpl
import dev.olog.msc.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    companion object {

        @Provides
        fun provideConfig() = Config(
            isDebug = BuildConfig.DEBUG,
            versionCode = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME,
            lastFmBaseUrl = "http://ws.audioscrobbler.com/2.0/",
            lastFmKey = BuildConfig.LAST_FM_KEY,
            lastFmSecret = BuildConfig.LAST_FM_SECRET,
            aesPassword = BuildConfig.AES_PASSWORD,
        )

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
    }

}