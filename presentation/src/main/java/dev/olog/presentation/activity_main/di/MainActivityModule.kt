package dev.olog.presentation.activity_main.di

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.dagger.ActivityLifecycle
import dev.olog.presentation.service_music.MediaControllerProvider

@Module
class MainActivityModule(
        private val activity: MainActivity
) {

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    @ActivityLifecycle
    internal fun provideLifecycle() : Lifecycle = activity.lifecycle

    @Provides
    internal fun provideFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

    @Provides
    internal fun provideActivity(): AppCompatActivity = activity

    @Provides
    internal fun provideFragmentActivity() : FragmentActivity = activity

    @Provides
    internal fun provideMusicControllerProvider(): MediaControllerProvider = activity

}