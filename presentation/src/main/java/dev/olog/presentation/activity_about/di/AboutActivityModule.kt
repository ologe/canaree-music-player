package dev.olog.presentation.activity_about.di

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_about.AboutActivity
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.ActivityLifecycle

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