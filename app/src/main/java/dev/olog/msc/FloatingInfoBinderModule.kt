package dev.olog.msc

import dagger.Module
import dagger.Provides
import dev.olog.floating_info.ActivityClass
import dev.olog.presentation.activity_main.MainActivity

@Module
class FloatingInfoBinderModule {

    @Provides
    internal fun provideActivityClass(): ActivityClass {
        return object : ActivityClass {
            override fun get(): Class<*> = MainActivity::class.java
        }
    }

}