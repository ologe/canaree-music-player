package dev.olog.injection

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.IEncrypter

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class CoreModule {

    @Binds
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
    }

}