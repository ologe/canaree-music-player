package dev.olog.msc.dagger

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.domain.IEncrypter
import dev.olog.msc.EncrypterImpl
import dev.olog.msc.app.App

@Module
abstract class AppModule {

    @Binds
    internal abstract fun provideContext(instance: App): Context

    @Binds
    internal abstract fun provideEncrypter(impl: EncrypterImpl): IEncrypter

    // TODO remove these
    companion object {

        @Provides
        internal fun provideResources(instance: App): Resources = instance.resources

        @Provides
        internal fun provideContentResolver(instance: App): ContentResolver {
            return instance.contentResolver
        }

        @Provides
        internal fun provideConnectivityManager(instance: App): ConnectivityManager {
            return instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }

}