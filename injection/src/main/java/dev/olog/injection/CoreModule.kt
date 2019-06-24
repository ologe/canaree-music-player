package dev.olog.injection

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import java.text.Collator
import java.util.*

@Module
abstract class CoreModule {

    @Binds
    @ApplicationContext
    internal abstract fun provideContext(instance: Application): Context

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

        @Provides
        @JvmStatic
        fun provideCollator(): Collator {
            val instance = Collator.getInstance(Locale.UK)
            instance.strength = Collator.SECONDARY
            return instance
        }
    }

}