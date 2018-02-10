package dev.olog.msc.module

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dev.olog.msc.App
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.dagger.ProcessLifecycle

@Module
class AppModule(
        private val app: App

) {

    @Provides
    @ApplicationContext
    fun provideContext() : Context = app

    @Provides
    fun provideResources(): Resources = app.resources

    @Provides
    fun provideApplication(): Application = app

    @Provides
    fun provideContentResolver(): ContentResolver = app.contentResolver

    @Provides
    @ProcessLifecycle
    fun provideAppLifecycle(): Lifecycle {
        return ProcessLifecycleOwner.get().lifecycle
    }

}