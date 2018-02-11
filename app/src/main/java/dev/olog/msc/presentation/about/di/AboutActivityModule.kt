package dev.olog.msc.presentation.about.di

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.qualifier.ActivityContext
import dev.olog.msc.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.presentation.about.AboutActivity

@Module
class AboutActivityModule(
        private val activity: AboutActivity
) {

    @Provides
    @ActivityLifecycle
    fun provideLifecycle() : Lifecycle = activity.lifecycle

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    @ActivityContext
    fun provideContext(): Context = activity

}