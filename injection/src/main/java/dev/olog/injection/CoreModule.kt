package dev.olog.injection

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.core.IEncrypter
import dev.olog.shared.dagger.ApplicationContext

@Module
abstract class CoreModule {

    @Binds
    @dev.olog.shared.dagger.ApplicationContext
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
        fun provideConnectivityManager(instance: Application): ConnectivityManager {
            return instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }

        @Provides
        @JvmStatic
        fun provideAlarmManager(instance: Application): AlarmManager {
            return instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
    }

}