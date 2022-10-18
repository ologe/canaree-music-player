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
import dev.olog.msc.BuildConfig
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Binds
    @Singleton
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    @Binds
    abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

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
        fun provideConfig() = Config(
            versionCode = BuildConfig.VERSION_CODE,
            versionName = BuildConfig.VERSION_NAME,
        )

    }

}