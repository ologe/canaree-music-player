package dev.olog.msc

import dagger.Module
import dagger.Provides
import dev.olog.floating_info.ActivityClass
import dev.olog.floating_info.FloatingInfoService
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.service_floating_info.FloatingInfoServiceBinder

@Module
class FloatingInfoBinderModule {

    @Provides
    internal fun provideActivityClass(): ActivityClass {
        return object : ActivityClass {
            override fun get(): Class<*> = MainActivity::class.java
        }
    }

    @Provides
    internal fun providerFloatingInfoServiceBinder()
            : FloatingInfoServiceBinder {
        return object : FloatingInfoServiceBinder {
            override fun get(): Class<*> = FloatingInfoService::class.java
        }
    }

}