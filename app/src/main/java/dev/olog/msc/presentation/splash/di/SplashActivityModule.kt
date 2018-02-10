package dev.olog.msc.presentation.splash.di

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.ActivityContext
import dev.olog.msc.presentation.splash.SplashActivity

@Module
class SplashActivityModule(
        private val activity: SplashActivity
) {

    @Provides
    @ActivityContext
    internal fun provideContext(): Context = activity

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    fun provideFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }

}