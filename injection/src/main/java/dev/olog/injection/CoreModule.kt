package dev.olog.injection

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.core.IEncrypter
import dev.olog.shared.ApplicationContext

@Module
abstract class CoreModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(instance: Application): Context

    @Binds
    abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideResources(instance: Application): Resources = instance.resources

        @Provides
        @JvmStatic
        internal fun provideContentResolver(instance: Application): ContentResolver {
            return instance.contentResolver
        }

        @Provides
        @JvmStatic
        fun provideConnectivityManager(instance: Application): ConnectivityManager {
            return instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }

}