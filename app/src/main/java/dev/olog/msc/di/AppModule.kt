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
import dev.olog.core.schedulers.Schedulers
import dev.olog.msc.BuildConfig
import dev.olog.msc.EncrypterImpl
import dev.olog.msc.SchedulersProd
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    companion object {
        @Provides
        fun provideNotificationManager(app: Application): NotificationManager {
            return app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        @Provides
        fun provideConnectivityManager(instance: Application): ConnectivityManager {
            return instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        @Provides
        fun provideConfig() = Config(
            isDebug = BuildConfig.DEBUG,
            versionName = BuildConfig.VERSION_NAME,
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
    }

    // TODO move to a different place
    @Binds
    fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

    @Binds
    fun provideSchedulers(impl: SchedulersProd): Schedulers

    @Binds
    @Singleton
    fun provideEncrypter(impl: EncrypterImpl): IEncrypter

}